package com.chat.client.controller;

import com.chat.client.core.Client;
import com.chat.client.view.ClientView;
import com.chat.client.view.HistoryView;

import java.rmi.RemoteException;

public class ClientFactory {
    private static Client clientInstance;

    public synchronized static Client getInstance() throws RemoteException {
        if (clientInstance == null) {
            clientInstance = new ClientController();
            clientInstance.setClientView(new ClientView(clientInstance));
            clientInstance.setHistoryView(new HistoryView(clientInstance));
        }

        return clientInstance;
    }
}
