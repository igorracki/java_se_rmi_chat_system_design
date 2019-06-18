package com.chat.server.controller;

import com.chat.server.core.Server;

public class ServerFactory {

    private static Server serverInstance;

    public synchronized static Server getInstance() {
        if (serverInstance == null) {
            serverInstance = new ServerController();
        }

        return serverInstance;
    }
}
