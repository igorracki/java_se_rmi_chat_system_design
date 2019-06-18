package com.eracigo.server.controller;

import static com.eracigo.util.Utility.ACCEPTING_PORT;
import static com.eracigo.util.Utility.EXPORT_PORT;
import static com.eracigo.util.Utility.MESSAGE_FORMAT;
import static com.eracigo.util.Utility.MESSAGE_TIME_FORMAT;
import static com.eracigo.util.Utility.SERVER_PRIVATE_MESSAGE_FORMAT;
import static com.eracigo.util.Utility.SERVICE_NAME;
import static com.eracigo.util.Utility.USERNAME_TAKEN;
import static com.eracigo.util.Utility.USER_JOINED_CHAT;
import static com.eracigo.util.Utility.USER_LEFT_CHAT;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eracigo.client.core.ClientAPI;
import com.eracigo.server.core.Server;
import com.eracigo.server.core.ServerAPI;
import com.eracigo.server.db.DatabaseUtility;
import com.eracigo.server.db.MessageModel;

public class ServerController implements Server {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerController.class);
    private final Map<String, ClientAPI> clients = new ConcurrentHashMap<>();
    private final DatabaseUtility databaseUtility;

    ServerController() {
        databaseUtility = new DatabaseUtility();
    }

    @Override
    public void startChatServer() throws RemoteException {
        final ServerAPI stub = (ServerAPI) UnicastRemoteObject.exportObject(this, EXPORT_PORT);
        final Registry registry = LocateRegistry.createRegistry(ACCEPTING_PORT);
        registry.rebind(SERVICE_NAME, stub);
        LOGGER.info("Server is running...");
    }

    @Override
    public void joinChat(final ClientAPI client) throws RemoteException, IllegalArgumentException {
        final String username = client.getUsername();
        if (isClientUsernameUnique(username)) {
            clients.put(username, client);
            updateMembers(true, username, true);
        } else {
            throw new IllegalArgumentException(String.format(USERNAME_TAKEN, username));
        }
    }

    @Override
    public void leaveChat(final ClientAPI client) throws RemoteException {
        final String username = client.getUsername();
        final ClientAPI removedClient = clients.remove(username);
        if (removedClient != null) {
            updateMembers(false, username, true);
        }
    }

    @Override
    public void getOnlineMembers() throws RemoteException {
        updateMembers(false, "", false);
    }

    private void updateMembers(final boolean hasJoined, final String username, final boolean sendNotification) {
        final List<String> members = clients.keySet().stream().collect(Collectors.toList());
        final String message = sendNotification ? hasJoined ? USER_JOINED_CHAT : USER_LEFT_CHAT : "";

        clients.forEach((k, v) -> {
            try {
                v.updateMembers(String.format(message, username), members);
            } catch (final RemoteException e) {
                LOGGER.info("Failed to update members. For more info check the logs.");
                LOGGER.trace("updateMembers: {}", e);
            }
        });
    }

    @Override
    public void sendToChat(final String username, final String message) throws RemoteException {
        final Date date = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(MESSAGE_TIME_FORMAT);
        final String messageToSend = String.format(MESSAGE_FORMAT, dateFormat.format(date), username, message);
        sendMessageToRemainingClients(username, messageToSend);
        databaseUtility.insertMessageToDB(username, message);
    }

    private void sendMessageToRemainingClients(final String username, final String message) {
        clients.forEach((k, v) -> {
            try {
                if (!k.equals(username)) {
                    v.sendMessage(message);
                }
            } catch (final RemoteException e) {
                LOGGER.info("Failed to send the message. For more info check the logs.");
                LOGGER.trace("sendToChat: {}", e);
            }
        });
    }

    @Override
    public boolean isClientUsernameUnique(final String username) throws RemoteException {
        for (final Map.Entry<String, ClientAPI> entry : clients.entrySet()) {
            if (username.equals(entry.getKey())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void sendPrivateMessage(final String sender, final String receiver, final String message) throws RemoteException {
        final ClientAPI client = clients.get(receiver);
        if (client != null) {
            final Date date = new Date();
            final SimpleDateFormat dateFormat = new SimpleDateFormat(MESSAGE_TIME_FORMAT);
            final String messageToSend = String.format(SERVER_PRIVATE_MESSAGE_FORMAT, dateFormat.format(date), sender, message);
            client.sendMessage(messageToSend);
            databaseUtility.insertMessageToDB(sender, message);
        }
    }

    @Override
    public void getMessageHistory(final String username, String startDate, String endDate) throws RemoteException {
        final ClientAPI client = clients.get(username);
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (startDate.equals("") || endDate.equals("")) {
            startDate = "01/01/1970";
            endDate = "31/12/2099";
        }

        if (client != null) {
            final List<MessageModel> messagesList = databaseUtility.getMessagesForUser(username, new Date(startDate), new Date(endDate));
            final List<String> messages = new ArrayList<>();
            messagesList.forEach((message) -> {
                messages.add(format.format(message.getTimestamp()) + ": " + message.getMessage());
            });
            client.sendMessageHistory(messages);
        }
    }
}
