package com.chat.server.core;

import java.rmi.RemoteException;

public interface ServerSPI {

    void startChatServer() throws RemoteException;

    boolean isClientUsernameUnique(final String username) throws RemoteException;

}
