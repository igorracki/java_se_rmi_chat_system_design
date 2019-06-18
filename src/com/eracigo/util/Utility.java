package com.eracigo.util;

public class Utility {
    public final static String USERNAME_TAKEN = "The username '%s' has already been taken!";
    public final static String MESSAGE_TIME_FORMAT = "HH:mm";
    public final static String MESSAGE_FORMAT = "<b><i>%s: %s</b></i><br>%s";
    public final static String MY_MESSAGE_FORMAT = "<p style=text-align: right;><b><i>%s</i></b><br>%s</p>";
    public final static String NOTIFICATION_FORMAT = "<center style=\"font-style:italic; color:blue;\">%s</center>";
    public final static String PRIVATE_MESSAGE_TAG = "[PRIVATE] ";
    public final static String SERVICE_NAME = "Igor's Chat Server";
    public final static String CONNECTION_FAILURE_MESSAGE = "Could not join the chat due to the following error:\n\n%s \n\nRetry?";
    public final static String MAX_RETRY = "Maximum number of retries reached.";
    public final static String USER_JOINED_CHAT = "%s has joined the chat!\n";
    public final static String USER_LEFT_CHAT = "%s has left the chat!\n";
    public final static String CLIENT_MESSAGE_FORMAT = "<p>%s</p>";
    public final static String CLIENT_PRIVATE_MESSAGE_FORMAT = "<p style=\"color: purple;\">%s</p>";
    public final static String CLIENT_MY_PRIVATE_MESSAGE_FORMAT =
            "<p style=\"text-align:right; color:purple;\">" + PRIVATE_MESSAGE_TAG + "to <b><i>%s %s</i></b><br>%s</p>";
    public final static String SERVER_PRIVATE_MESSAGE_FORMAT = PRIVATE_MESSAGE_TAG + MESSAGE_FORMAT;
    public final static int EXPORT_PORT = 0;
    public final static int ACCEPTING_PORT = 1099;
}
