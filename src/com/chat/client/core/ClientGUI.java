package com.chat.client.core;

import java.util.List;

public interface ClientGUI {
    void initializeFrame();

    void appendMessage(final String message);

    void appendPrivateMessage(final String message);

    void showNotification(final String message);

    void setConnectedUsersList(final List<String> members);
}
