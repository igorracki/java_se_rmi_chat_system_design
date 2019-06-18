package com.chat.client;

import com.chat.client.user.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private static final String USERNAME = "testUser";
    private User user;

    @Before
    public void setup() {
        user = new User(USERNAME);
    }

    @Test
    public void testUserIsCorrectlyInstantiated() {
        assertEquals(USERNAME, user.getUsername());
    }
}
