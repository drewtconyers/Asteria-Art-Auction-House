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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ServerUpload adds new piece to Azure SQL database and sets on auction or purchase
 */
public class ServerUpload {
    String type;
    String artworkTitle;
    String owner;
    String encodedImage;
    String details;
    String bidHistory;

    double startingBid;
    double purchasePrice;

    int hour;
    int minute;

    /**
     * Constructor parsing information from client using GSON
     * @param input
     * @throws ParseException
     */
    public ServerUpload(String input) throws ParseException {
        Gson gson = new Gson();
        UploadMessage message = gson.fromJson(input, UploadMessage.class);
        this.type = message.type;
        this.artworkTitle = message.artworkTitle;
        this.owner = message.owner;
        this.encodedImage = message.encodedImage;
        this.details = message.details;
        this.startingBid = message.startingBid;
        this.purchasePrice = message.purchasePrice;
        this.bidHistory = message.bidHistory;
        this.hour = message.hour;
        this.minute = message.minute;

        databaseUpload();
    }

    /**
     * Insertion into SQL database
     * @throws ParseException
     */
    private void databaseUpload() throws ParseException {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        String uploadCmd = "INSERT INTO asteria.artwork (artwork_title, owner, encoded_image, details, purchase_price, bid_price, hour, minute, bid_history) VALUES ('" + artworkTitle +"', '" + owner + "', '" + encodedImage + "', '" + details + "', " + purchasePrice + ", "+ startingBid + ", " + hour + ", " + minute +", '" + bidHistory + "')";

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(uploadCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setEndTime();
        setOnAuction();
    }

    /**
     * Sets the end time of an auction
     * @throws ParseException
     */
    private void setEndTime() throws ParseException {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        Calendar current = Calendar.getInstance();
        current.setTime(currentTime);
        String startStrDate = dateFormat.format(currentTime);

        String startUploadCmd = "UPDATE asteria.artwork SET start_time = '" + startStrDate + "' WHERE bid_history = '" + bidHistory +"'";


        System.out.println("Auction Start Time: " + startStrDate);
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(startUploadCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);

        c.add(Calendar.MINUTE, minute);
        c.add(Calendar.HOUR, hour);

        Date endDate = c.getTime();
        System.out.println("Auction End Time: " + dateFormat.format(endDate));
        String endStrDate = dateFormat.format(endDate);

        String timeUploadCmd = "UPDATE asteria.artwork SET end_time = '" + endStrDate + "' WHERE bid_history = '" + bidHistory +"'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(timeUploadCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets piece on auction
     */
    private void setOnAuction() {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        String setOnAuctionCmd = "UPDATE asteria.artwork SET onAuction = 'TRUE' WHERE bid_history = '" + bidHistory +"'";

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setOnAuctionCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
