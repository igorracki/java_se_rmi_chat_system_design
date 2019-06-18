package com.eracigo.server;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eracigo.server.controller.ServerFactory;
import com.eracigo.server.core.Server;

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
