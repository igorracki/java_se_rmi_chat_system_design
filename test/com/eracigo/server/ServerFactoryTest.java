package com.eracigo.server;

import com.eracigo.server.core.Server;
import com.eracigo.server.controller.ServerController;
import com.eracigo.server.controller.ServerFactory;
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
