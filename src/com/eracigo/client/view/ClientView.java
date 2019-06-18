package com.eracigo.client.view;

import static com.eracigo.util.Utility.CLIENT_MESSAGE_FORMAT;
import static com.eracigo.util.Utility.CLIENT_MY_PRIVATE_MESSAGE_FORMAT;
import static com.eracigo.util.Utility.CLIENT_PRIVATE_MESSAGE_FORMAT;
import static com.eracigo.util.Utility.MESSAGE_TIME_FORMAT;
import static com.eracigo.util.Utility.MY_MESSAGE_FORMAT;
import static com.eracigo.util.Utility.NOTIFICATION_FORMAT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eracigo.client.core.Client;
import com.eracigo.client.core.ClientGUI;

public class ClientView extends JFrame implements ClientGUI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientView.class);
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Client controller;
    private final Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
    private final JPanel centerPanel;
    private final JPanel leftPanel;
    private final JPanel onlinePanel;
    private final JEditorPane incomingMessages;
    private final JTextField outgoingMessages;
    private final JScrollPane scroller;

    public ClientView(final Client controller) {
        super("Chat App");
        this.controller = controller;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        centerPanel = new JPanel();
        leftPanel = new JPanel();
        onlinePanel = new JPanel();
        incomingMessages = new JEditorPane();
        incomingMessages.setContentType("text/html");
        incomingMessages.setPreferredSize(new Dimension(550, 400));
        incomingMessages.setEditable(false);

        scroller = new JScrollPane(incomingMessages);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setAutoscrolls(true);

        outgoingMessages = new JTextField(20);
        outgoingMessages.requestFocusInWindow();
        outgoingMessages.addActionListener(new SendButtonListener());

        final JButton sendButton = new JButton("Send");
        final JButton historyButton = new JButton("Your Messages");
        sendButton.addActionListener(new SendButtonListener());
        historyButton.addActionListener(new HistoryButtonListener());

        centerPanel.add(scroller);
        centerPanel.add(outgoingMessages);
        centerPanel.add(sendButton);
        centerPanel.add(historyButton);

        onlinePanel.setBackground(Color.WHITE);
        onlinePanel.setLayout(new GridLayout(0, 1));
        leftPanel.add(new JLabel("ONLINE USERS", SwingConstants.CENTER));
        leftPanel.add(new JLabel("Click to send PM", SwingConstants.CENTER));
        leftPanel.add(onlinePanel);

        centerPanel.setPreferredSize(new Dimension(600, 500));
        leftPanel.setPreferredSize(new Dimension(200, 500));

        getContentPane().add(centerPanel, BorderLayout.LINE_END);
        getContentPane().add(leftPanel, BorderLayout.LINE_START);

        setSize(800, 500);
        setResizable(false);
        setLocation(screenSize.width / 2 - this.getSize().width / 2, screenSize.height / 2 - this.getSize().height / 2);
    }

    @Override
    public void initializeFrame() {
        setVisible(true);
    }

    @Override
    public void appendMessage(final String message) {
        final String outputMessage = String.format(CLIENT_MESSAGE_FORMAT, message);
        appendToPane(outputMessage);
    }

    private void appendMyMessage(final String message) {
        final Date date = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(MESSAGE_TIME_FORMAT);
        final String outputMessage = String.format(MY_MESSAGE_FORMAT, dateFormat.format(date), message);
        appendToPane(outputMessage);
    }

    @Override
    public void appendPrivateMessage(final String message) {
        final String outputMessage = String.format(CLIENT_PRIVATE_MESSAGE_FORMAT, message);
        appendToPane(outputMessage);
    }

    private void appendMyPrivateMessage(final String receiver, final String message) {
        final Date date = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(MESSAGE_TIME_FORMAT);
        final String outputMessage = String.format(CLIENT_MY_PRIVATE_MESSAGE_FORMAT, receiver, dateFormat.format(date), message);
        appendToPane(outputMessage);
    }

    @Override
    public void showNotification(final String message) {
        final String outputMessage = String.format(NOTIFICATION_FORMAT, message);
        appendToPane(outputMessage);
    }

    private void appendToPane(final String outputMessage) {
        final EditorKit editor = incomingMessages.getEditorKit();
        final StringReader reader = new StringReader(outputMessage);
        try {
            editor.read(reader, incomingMessages.getDocument(), incomingMessages.getDocument().getLength());
            scroller.getVerticalScrollBar().setValue(scroller.getVerticalScrollBar().getMaximum());
        } catch (final IOException | BadLocationException e) {
            LOGGER.info("Could not append the message to view. For more info check the logs.");
            LOGGER.trace("Could not append the message to view. {}", e);
        }
    }

    @Override
    public void setConnectedUsersList(final List<String> members) {
        onlinePanel.removeAll();
        members.forEach((member) -> {
            final JLabel username = new JLabel(member, SwingConstants.CENTER);
            username.addMouseListener(new PrivateMessageListener());
            username.setPreferredSize(new Dimension(125, 30));
            username.setBorder(border);
            username.setOpaque(true);
            username.addMouseListener(new MouseEvents());
            onlinePanel.add(username);
        });
        onlinePanel.revalidate();
        onlinePanel.repaint();
    }

    class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            final String message = outgoingMessages.getText();
            outgoingMessages.setText("");
            if (!message.equals("")) {
                try {
                    appendMyMessage(message);
                    controller.sendToChat(message);
                } catch (final RemoteException ex) {
                    LOGGER.info("Failed to send the message. For more info check the logs.");
                    LOGGER.trace("Failed to send the message. {}", ex);
                }
            }
        }
    }

    class HistoryButtonListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                controller.fireMessageHistoryView();
            } catch (final RemoteException ex) {
                LOGGER.info("Failed to open History View. For more info check the logs.");
                LOGGER.trace("Failed to open History View. {}", ex);
            }
        }
    }

    class PrivateMessageListener extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            final String receiver = ((JLabel) e.getSource()).getText();
            final String privateMessage = JOptionPane.showInputDialog(String.format("Send a private message to: %s", receiver));
            if (privateMessage != null && !privateMessage.equals("")) {
                try {
                    appendMyPrivateMessage(receiver, privateMessage);
                    controller.sendToUser(receiver, privateMessage);
                } catch (final RemoteException ex) {
                    LOGGER.info("Failed to send a private message. For more info check the logs.");
                    LOGGER.trace("Failed to send a private message. {}", ex);
                }
            }
        }
    }

    class MouseEvents extends MouseAdapter {
        @Override
        public void mouseEntered(final MouseEvent e) {
            final JLabel target = (JLabel) e.getSource();
            target.setBackground(Color.ORANGE);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            final JLabel target = (JLabel) e.getSource();
            target.setBackground(Color.WHITE);
        }
    }
}
