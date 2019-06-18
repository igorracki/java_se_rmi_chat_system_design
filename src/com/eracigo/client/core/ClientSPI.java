package com.eracigo.client.core;

import java.rmi.RemoteException;

public interface ClientSPI {
    void login() throws RemoteException;

    void setUsername(final String username) throws RemoteException;

    void sendToChat(final String message) throws RemoteException;

    void sendToUser(final String receiver, final String message) throws RemoteException;

    void fireMessageHistoryView() throws RemoteException;

    void requestMessageHistory(final String startDate, final String endDate) throws RemoteException;

    void setClientView(final ClientGUI view) throws RemoteException;

    void setHistoryView(final HistoryGUI view) throws RemoteException;
}
