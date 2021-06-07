/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Purchase manages user request for a new purchase
 */
public class Purchase {
    String type;
    String username;
    String owner;

    double purchasePrice;
    double user_funds;

    int piece_number;

    /**
     * Constructor method for new purchase
     * @param message
     */
    public Purchase(UploadMessage message) {
        this.type = message.type;
        this.username = message.username;
        this.purchasePrice = message.purchasePrice;
        this.piece_number = message.piece_number;
        this.user_funds = message.user_funds;
        this.owner = message.owner;
        processPurchase();
    }

    /**
     * Calls required methods to process purchase
     */
    private void processPurchase() {
        payment();
        changeOwner();
        updateHistory();
    }

    /**
     * Updates the transaction history of the server
     */
    private void updateHistory() {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        String historyCmd = "SELECT bid_history FROM asteria.artwork WHERE piece_number = " + piece_number;
        String history = "";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(historyCmd);
            while(queryResult.next()) {
                history = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes purchase between clients
     */
    private void payment() {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();

        double buyerFunds = user_funds;

        String ownerFundsCmd = "SELECT user_funds FROM asteria.login WHERE username = '" + owner + "'";
        double ownerFunds  = doubleSQL(ownerFundsCmd);

        buyerFunds -= purchasePrice;
        ownerFunds += purchasePrice;

        String setOwner = "UPDATE asteria.login SET user_funds = " + ownerFunds +" WHERE username = '" + owner + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setOwner);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String setBuyer = "UPDATE asteria.login SET user_funds = " + buyerFunds +" WHERE username = '" + username + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setBuyer);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Changes owner of artwork in database
     */
    private void changeOwner() {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        String setOwner = "UPDATE asteria.artwork SET owner = '" + username +"' WHERE piece_number = " + piece_number;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setOwner);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Abstract method to get double from database
     * @param cmd
     * @return
     */
    public static double doubleSQL(String cmd) {
        double result = 0;
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(cmd);
            while(queryResult.next()) {
                result = queryResult.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
