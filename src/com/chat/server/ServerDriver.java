package com.chat.server;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.server.controller.ServerFactory;
import com.chat.server.core.Server;

public class ServerDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDriver.class);

    public static void main(final String[] args) {
        final Server server = ServerFactory.getInstance();
        try {
            server.startChatServer();
        } catch (final RemoteException e) {
            LOGGER.info("Failed to start the server. For more info check the logs.");
            LOGGER.trace("Failed to start the server. {}");
        }
    }
}
