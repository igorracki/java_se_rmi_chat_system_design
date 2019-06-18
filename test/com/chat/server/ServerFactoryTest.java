package com.chat.server;

import com.chat.server.core.Server;
import com.chat.server.controller.ServerController;
import com.chat.server.controller.ServerFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServerFactoryTest {

    private Server server;

    @Before
    public void setup() {
        server = ServerFactory.getInstance();
    }

    @Test
    public void testFactoryReturnedCorrectImplementation() {
        assertTrue(server instanceof ServerController);
    }
}
