package com.chat.client;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.client.controller.ClientFactory;
import com.chat.client.core.Client;

public class ClientDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDriver.class);

    public static void main(final String[] args) {
        try {
            final Client client = ClientFactory.getInstance();
            client.login();
        } catch (final RemoteException e) {
            LOGGER.info("Failed to start the client. For more info check the logs.");
            LOGGER.trace("Failed to start the client. {}", e);
        }
    }
}
