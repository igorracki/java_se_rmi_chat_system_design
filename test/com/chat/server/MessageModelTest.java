package com.chat.server;

import com.chat.server.db.MessageModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageModelTest {

    private static final String USERNAME = "testUser";
    private static final String MESSAGE = "testMessage";
    private static final long TIMESTAMP = 1;
    private MessageModel messageModel;

    @Before
    public void setup() {
        messageModel = new MessageModel(USERNAME, MESSAGE, TIMESTAMP);
    }

    @Test
    public void testMessageModelIsCorrectlyInstantiated() {
        assertEquals(USERNAME, messageModel.getUsername());
        assertEquals(MESSAGE, messageModel.getMessage());
        assertEquals(TIMESTAMP, messageModel.getTimestamp());
    }
}
