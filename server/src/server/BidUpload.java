/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package server;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Processes new bids on artwork in a thread safe way
 */
public class BidUpload implements Runnable{
    static Object lock;

    String type;
    String bidderName;
    String artworkTitle;
    String bidHistory;

    int piece_number;

    double bidUSD;

    /**
     * Constructor Method
     * @param input
     * @throws ParseException
     */
    public BidUpload(String input) throws ParseException {
        Gson gson = new Gson();
        UploadMessage message = gson.fromJson(input, UploadMessage.class);
        this.type = message.type;
        this.bidderName = message.bidderName;
        this.artworkTitle = message.artworkTitle;
        this.bidUSD = message.bidUSD;
        this.piece_number = message.piece_number;
        this.bidHistory = message.bidHistory;
        this.lock = new Object();
    }

    /**
     * Processes bids in thread safe way by locking clients in a que for new bid
     */
    @Override
    public void run() {
        synchronized (lock) {
            syncAddBid();
        }
    }

    /**
     * Updates database with new bid information
     */
    public synchronized void syncAddBid() {
        //set current highest bidder, bid_price, update bid_history
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        String updateCmd = "UPDATE asteria.artwork SET current_highest_bidder = '"+ bidderName + "' WHERE piece_number = " + piece_number;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(updateCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
        Date date = new Date();
        String history = bidHistory + "\n" + bidderName + " bid $" + bidUSD + " on this piece at " + dateFormat.format(date);
        //System.out.println(history);
        String historyUpdateCmd = "UPDATE asteria.artwork SET bid_history = '"+ history + "' WHERE piece_number = " + piece_number;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(historyUpdateCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String priceUpdateCmd = "UPDATE asteria.artwork SET bid_price = " + bidUSD + " WHERE piece_number = " + piece_number;
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(priceUpdateCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String result = "";
        String serverHistoryCmd = "SELECT server_history FROM asteria.transactions";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(serverHistoryCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String serverHistory = bidderName + " bid $" + bidUSD + " on " + artworkTitle + " at " + dateFormat.format(date) + "\n" + result;
        //System.out.println(serverHistory);
        String updateServerHistory = "UPDATE asteria.transactions SET server_history = '" + serverHistory + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(updateServerHistory);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int resultInt = 0;
        String bidAmountCmd = "SELECT bids_amount FROM asteria.artwork WHERE piece_number = " + piece_number;

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(bidAmountCmd);
            while(queryResult.next()) {
                resultInt = queryResult.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String bidAmountStr = "UPDATE asteria.artwork SET bids_amount = " + (resultInt + 1) + " WHERE piece_number = " + piece_number;
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(bidAmountStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
