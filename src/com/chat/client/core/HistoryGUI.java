package com.chat.client.core;

import java.util.List;

public interface HistoryGUI {
    void initializeFrame();

    void updateHistory(final List<String> history);
}
