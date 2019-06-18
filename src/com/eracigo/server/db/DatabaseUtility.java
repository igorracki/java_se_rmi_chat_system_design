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

package com.eracigo.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DatabaseUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtility.class);

    public DatabaseUtility() {
        loadDriver();
    }

    public void insertMessageToDB(final String username, final String message) {
        final Connection connection = getConnection();
        final Date date = new Date();
        final String query = "INSERT INTO history (username, message, timestamp) VALUES (?, ?, ?)";

        if (connection != null) {
            try {
                final PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, message);
                preparedStatement.setLong(3, date.getTime());

                preparedStatement.executeUpdate();
                preparedStatement.close();

                connection.close();
            } catch (final SQLException e) {
                LOGGER.info("Failed to insert a row. For more info check the logs.");
                LOGGER.trace("Failed to insert a row. {}", e);
            }
        }
    }

    public List<MessageModel> getMessagesForUser(final String username, final Date start, final Date end) {
        final Connection connection = getConnection();
        final String query =
                "SELECT * FROM history WHERE username = '" + username + "' AND (TIMESTAMP >= " + start.getTime() + " AND TIMESTAMP <= " + end
                        .getTime() + ")";
        final List<MessageModel> messages = new ArrayList<>();

        if (connection != null) {
            try {
                final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    messages.add(new MessageModel(
                            resultSet.getString("username"),
                            resultSet.getString("message"),
                            resultSet.getLong("timestamp")
                    ));
                }

                statement.close();
                resultSet.close();
                connection.close();

                return messages;
            } catch (final SQLException e) {
                LOGGER.info("Failed to execute a select query. For more info check the logs.");
                LOGGER.trace("Failed to execute a seelct query. {}", e);
            }
        }

        return null;
    }

    private void loadDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (final ClassNotFoundException e) {
            LOGGER.info("Failed to load the DB driver. For more info check the logs.");
            LOGGER.trace("Failed to load the DB driver. {}", e);
        }
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "root", "");
        } catch (final SQLException e) {
            LOGGER.info("Cannot establish a database connection. For more info check the logs.");
            LOGGER.trace("Cannot establish a databaase connection. {}", e);
        }

        return null;
    }
}
