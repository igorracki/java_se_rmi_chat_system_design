package com.eracigo.server.controller;

import com.eracigo.server.core.Server;

public class ServerFactory {

    private static Server serverInstance;

    public synchronized static Server getInstance() {
        if (serverInstance == null) {
            serverInstance = new ServerController();
        }

        return serverInstance;
    }
}
