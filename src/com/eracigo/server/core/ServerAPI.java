package com.eracigo.server.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.eracigo.client.core.ClientAPI;

public interface ServerAPI extends Remote {

    void joinChat(final ClientAPI client) throws RemoteException, IllegalArgumentException;

    void leaveChat(final ClientAPI client) throws RemoteException;

    void sendToChat(final String username, final String message) throws RemoteException;

    void getOnlineMembers() throws RemoteException;

    void sendPrivateMessage(final String sender, final String receiver, final String message) throws RemoteException;

    void getMessageHistory(final String username, String startDate, String endDate) throws RemoteException;
}
