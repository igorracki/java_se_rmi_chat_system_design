/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.eracigo.client;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.eracigo.client.controller.ClientFactory;
import com.eracigo.client.core.Client;
import com.eracigo.client.core.ClientGUI;
import com.eracigo.client.core.HistoryGUI;
import com.eracigo.server.core.ServerAPI;

/**
 *
 */
public class ClientControllerTest {

    private Client client;
    private ServerAPI server;
    private ClientGUI clientGUI;
    private HistoryGUI historyGUI;

    @Before
    public void setup() throws RemoteException {
        client = ClientFactory.getInstance();
        server = mock(ServerAPI.class);
        clientGUI = mock(ClientGUI.class);
        historyGUI = mock(HistoryGUI.class);
        client.setClientView(clientGUI);
        client.setHistoryView(historyGUI);
    }

    @Test
    public void testPublicMessageIsDetermined() throws RemoteException {
        client.sendMessage("testMessage");
        verify(clientGUI, times(1)).appendMessage("testMessage");
    }

    @Test
    public void testPrivateMessageIsDetermined() throws RemoteException {
        client.sendMessage("[PRIVATE] testMessage");
        verify(clientGUI, times(1)).appendPrivateMessage("[PRIVATE] testMessage");
        verify(clientGUI, times(0)).appendMessage(anyString());
    }

    @Test
    public void testMembersAreUpdatedInGUI() throws RemoteException {
        client.updateMembers("someMessage", new ArrayList<>());
        verify(clientGUI, times(1)).showNotification("someMessage");
        verify(clientGUI, times(1)).setConnectedUsersList(anyListOf(String.class));
    }

    @Test
    public void testHistoryGUIrequestInitializesFrame() throws RemoteException {
        client.fireMessageHistoryView();
        verify(historyGUI, times(1)).initializeFrame();
    }

    @Test
    public void testHistoryIsUpdatedInTheGUI() throws RemoteException {
        client.sendMessageHistory(new ArrayList<>());
        verify(historyGUI, times(1)).updateHistory(anyListOf(String.class));
    }
}
