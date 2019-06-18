package com.chat.client.controller;

import static javax.swing.JOptionPane.YES_OPTION;

import static com.chat.util.Utility.CONNECTION_FAILURE_MESSAGE;
import static com.chat.util.Utility.EXPORT_PORT;
import static com.chat.util.Utility.MAX_RETRY;
import static com.chat.util.Utility.PRIVATE_MESSAGE_TAG;
import static com.chat.util.Utility.SERVICE_NAME;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.client.core.Client;
import com.chat.client.core.ClientAPI;
import com.chat.client.core.ClientGUI;
import com.chat.client.core.HistoryGUI;
import com.chat.client.user.User;
import com.chat.server.core.ServerAPI;

public class ClientController implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    private ClientAPI stub;
    private ServerAPI server;
    private ClientGUI clientView;
    private HistoryGUI historyView;
    private User user;

    ClientController() {
        setExitBehavior();
    }

    @Override
    public void login() throws RemoteException {
        joinChatServer();
        attemptLogin(0);
        clientView.initializeFrame();
        server.getOnlineMembers();
    }

    private void attemptLogin(int attempts) throws RemoteException {
        if (attempts < 3) {
            final String username = JOptionPane.showInputDialog("Enter your username.");
            try {
                user = new User(username);
                server.joinChat(stub);
            } catch (final IllegalArgumentException ex) {
                final int result = JOptionPane.showConfirmDialog(null, String.format(CONNECTION_FAILURE_MESSAGE, ex.getMessage()));
                if (result == YES_OPTION) {
                    attemptLogin(++attempts);
                } else {
                    System.exit(0);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error", MAX_RETRY, JOptionPane.ERROR_MESSAGE);
            LOGGER.info("Failed to join the chat. For more info check the logs.");
            LOGGER.trace("Failed to join the chat. {}", MAX_RETRY);
            System.exit(0);
        }
    }

    private void joinChatServer() {
        try {
            stub = (ClientAPI) UnicastRemoteObject.exportObject(this, EXPORT_PORT);
            final Registry registry = LocateRegistry.getRegistry();
            server = (ServerAPI) registry.lookup(SERVICE_NAME);
        } catch (final RemoteException | NotBoundException e) {
            LOGGER.info("Failed to join the chat server. For more info check the logs.");
            LOGGER.trace("Failed to join the chat server. {}", e);
        }
    }

    @Override
    public String getUsername() throws RemoteException {
        return user != null ? user.getUsername() : "";
    }

    @Override
    public void setUsername(final String username) throws RemoteException {
        user.setUsername(username);
    }

    @Override
    public void sendMessage(final String message) throws RemoteException {
        if (message.contains(PRIVATE_MESSAGE_TAG)) {
            clientView.appendPrivateMessage(message);
        } else {
            clientView.appendMessage(message);
        }
    }

    @Override
    public void updateMembers(final String message, final List<String> members) throws RemoteException {
        if (clientView != null) {
            clientView.showNotification(message);
            clientView.setConnectedUsersList(members);
        }
    }

    @Override
    public void sendToChat(final String message) throws RemoteException {
        server.sendToChat(getUsername(), message);
    }

    @Override
    public void sendToUser(final String receiver, final String message) throws RemoteException {
        server.sendPrivateMessage(getUsername(), receiver, message);
    }

    @Override
    public void fireMessageHistoryView() {
        historyView.initializeFrame();
    }

    @Override
    public void sendMessageHistory(final List<String> messages) throws RemoteException {
        historyView.updateHistory(messages);
    }

    @Override
    public void requestMessageHistory(final String startDate, final String endDate) throws RemoteException {
        server.getMessageHistory(getUsername(), startDate, endDate);
    }

    @Override
    public void setClientView(final ClientGUI view) throws RemoteException {
        clientView = view;
    }

    @Override
    public void setHistoryView(final HistoryGUI view) throws RemoteException {
        historyView = view;
    }

    private void setExitBehavior() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (server != null) {
                    server.leaveChat(this);
                }
            } catch (final RemoteException e) {
                LOGGER.info("Failed to leave the chat. For more info check the logs.");
                LOGGER.trace("Failed to leave the chat. {}", e);
            }
        }));
    }
}
