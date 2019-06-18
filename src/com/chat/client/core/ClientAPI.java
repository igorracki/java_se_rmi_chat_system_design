package com.chat.client.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientAPI extends Remote {
    String getUsername() throws RemoteException;

    void sendMessage(final String message) throws RemoteException;

    void updateMembers(final String message, final List<String> members) throws RemoteException;

    void sendMessageHistory(final List<String> messages) throws RemoteException;
}
