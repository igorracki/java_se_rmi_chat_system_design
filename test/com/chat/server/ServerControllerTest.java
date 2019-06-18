package com.chat.server;

import com.chat.client.core.ClientAPI;
import com.chat.server.core.Server;
import com.chat.server.controller.ServerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.RemoteException;

import static com.chat.util.Utility.PRIVATE_MESSAGE_TAG;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ServerControllerTest {

    private Server server;
    private ClientAPI client;
    private ClientAPI client2;
    private ClientAPI client3;

    @BeforeClass
    public static void start() throws RemoteException {
        // a "pretend integration test" for the database functionality
        ServerFactory.getInstance().startChatServer();
    }

    @Before
    public void setup() throws RemoteException {
        server = ServerFactory.getInstance();
        client = mock(ClientAPI.class);
        client2 = mock(ClientAPI.class);
        client3 = mock(ClientAPI.class);
        when(client.getUsername()).thenReturn("testUser");
        when(client2.getUsername()).thenReturn("testUser2");
        when(client3.getUsername()).thenReturn("testUser3");
    }

    @After
    public void cleanup() throws RemoteException {
        server.leaveChat(client);
        server.leaveChat(client2);
    }

    @Test
    public void testClientIsAddedSuccessfully() {
        try {
            when(client.getUsername()).thenReturn("testUser");
            server.joinChat(client);
        } catch (final Exception e) {
            fail("Should not have thrown an exception.");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUsernameMustBeUnique() throws RemoteException {
        when(client2.getUsername()).thenReturn("testUser");
        server.joinChat(client);
        server.joinChat(client2);
    }

    @Test
    public void testWhenClientJoinsChatMembersAreUpdated() throws RemoteException {
        server.joinChat(client);
        verify(client, times(1)).updateMembers(anyString(), anyListOf(String.class));
    }

    @Test
    public void testWhenClientLeavesChatMembersAreUpdated() throws RemoteException {
        server.joinChat(client);
        server.joinChat(client2);
        server.leaveChat(client2);
        verify(client, times(3)).updateMembers(anyString(), anyListOf(String.class));
    }

    @Test
    public void testDoNotUpdateWhenClientDoesNotExist() throws RemoteException {
        server.joinChat(client);
        server.leaveChat(client2);
        verify(client, times(1)).updateMembers(anyString(), anyListOf(String.class));
    }

    @Test
    public void testInvokingGetOnlineMembersUpdatesTheClient() throws RemoteException {
        server.joinChat(client);
        server.getOnlineMembers();
        verify(client, times(2)).updateMembers(anyString(), anyListOf(String.class));
    }

    @Test
    public void testWhenClientSendsAMessageTheMessageIsSentToAllClients() throws RemoteException {
        server.joinChat(client);
        server.joinChat(client2);
        server.sendToChat(client.getUsername(), "testMessage");
        verify(client2, times(1)).sendMessage(contains("testMessage"));
    }

    @Test
    public void testThatSenderDoesNotReceiveItsOwnMessage() throws RemoteException {
        server.joinChat(client);
        server.joinChat(client2);
        server.sendToChat(client.getUsername(), "testMessage");
        verify(client, times(0)).sendMessage(anyString());
    }

    @Test
    public void testClientCanSendPrivateMessage() throws RemoteException {
        server.joinChat(client);
        server.joinChat(client2);
        server.joinChat(client3);
        server.sendPrivateMessage(client.getUsername(), client2.getUsername(), "testMessage");
        verify(client2, times(1)).sendMessage(contains(PRIVATE_MESSAGE_TAG));
        verify(client3, times(0)).sendMessage(anyString());
    }

    @Test
    public void testGetMessageHistoryForUser() throws RemoteException {
        server.joinChat(client);
        server.sendToChat(client.getUsername(), "testMessage");

        try {
            server.getMessageHistory(client.getUsername(), "", "");
            verify(client, times(1)).sendMessageHistory(anyListOf(String.class));
        } catch (final Exception e) {
            verify(client, times(0)).sendMessageHistory(any());
        }
    }

    @Test
    public void testGetMessageInPeriodHistoryForUser() throws RemoteException {
        server.joinChat(client);
        server.sendToChat(client.getUsername(), "testMessage");

        try {
            server.getMessageHistory(client.getUsername(), "03/03/2019", "05/05/2019");
            verify(client, times(1)).sendMessageHistory(anyListOf(String.class));
        } catch (final Exception e) {
            verify(client, times(0)).sendMessageHistory(any());
        }
    }
}
