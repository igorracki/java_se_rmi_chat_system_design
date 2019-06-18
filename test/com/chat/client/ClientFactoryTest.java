package com.chat.client;

import com.chat.client.core.Client;
import com.chat.client.controller.ClientController;
import com.chat.client.controller.ClientFactory;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.assertTrue;

public class ClientFactoryTest {

    private Client client;

    @Before
    public void setup() throws RemoteException {
        client = ClientFactory.getInstance();
    }

    @Test
    public void testFactoryReturnedCorrectImplementation() {
        assertTrue(client instanceof ClientController);
    }
}
