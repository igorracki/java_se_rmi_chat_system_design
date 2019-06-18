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

package com.chat.client.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.client.core.Client;
import com.chat.client.core.HistoryGUI;

/**
 *
 */
public class HistoryView extends JFrame implements HistoryGUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryView.class);
    private final Client controller;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final JPanel messagePanel;
    private final JPanel selectionPanel;
    private final JTextArea textArea;
    private final JButton allButton;
    private final JButton dateButton;
    private final JTextField dateStartField;
    private final JTextField dateEndField;

    public HistoryView(final Client controller) {
        super("Chat History");
        this.controller = controller;
        messagePanel = new JPanel();
        selectionPanel = new JPanel();
        allButton = new JButton("Show Full History");
        dateButton = new JButton("Show By Date");
        textArea = new JTextArea(25, 25);
        dateStartField = new JTextField();
        dateEndField = new JTextField();

        selectionPanel.setPreferredSize(new Dimension(150, 500));
        messagePanel.setPreferredSize(new Dimension(300, 500));
        allButton.addActionListener(new HistoryButtonListener());
        dateButton.addActionListener(new HistoryButtonListener());

        messagePanel.add(textArea);
        selectionPanel.add(allButton);
        selectionPanel.add(new JLabel());
        selectionPanel.add(new JLabel("Start Date: dd/mm/yyyy"));
        selectionPanel.add(dateStartField);
        selectionPanel.add(new JLabel("End Date: dd/mm/yyyy"));
        selectionPanel.add(dateEndField);
        selectionPanel.add(dateButton);
        selectionPanel.setLayout(new GridLayout(8, 1));
        getContentPane().add(messagePanel, BorderLayout.LINE_END);
        getContentPane().add(selectionPanel, BorderLayout.LINE_START);
        setSize(500, 500);
        setLocation(screenSize.width / 2 - this.getSize().width / 2, screenSize.height / 2 - this.getSize().height / 2);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    public void initializeFrame() {
        setVisible(true);
    }

    @Override
    public void updateHistory(final List<String> history) {
        history.forEach(message -> textArea.append(message + "\n"));
    }

    class HistoryButtonListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            textArea.setText("");
            try {
                controller.requestMessageHistory(dateStartField.getText(), dateEndField.getText());
            } catch (final RemoteException ex) {
                LOGGER.info("Failed to request message history. For more info check the logs.");
                LOGGER.trace("Failed to request message hisotry. {}", ex);
            }
        }
    }
}
