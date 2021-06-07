/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package server;

import com.google.gson.Gson;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Main server class. Creates and manages connection to clients
 */
public class Server {
    static public ArrayList<ClientHandler> clientList;

    public static void main(String[] args) {
        new Server().runServer();
    }

    private void runServer() {
        try {
            clientList = new ArrayList<ClientHandler>();
            startingPrint();
            setUpNetworking();
            /*
            Get port from user
             */
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void startingPrint() {
        System.out.println("\n" +
                "                                                                                                                  \n" +
                "         .8.            d888888o. 8888888 8888888888 8 8888888888   8 888888888o.    8 8888          .8.          \n" +
                "        .888.         .`8888:' `88.     8 8888       8 8888         8 8888    `88.   8 8888         .888.         \n" +
                "       :88888.        8.`8888.   Y8     8 8888       8 8888         8 8888     `88   8 8888        :88888.        \n" +
                "      . `88888.       `8.`8888.         8 8888       8 8888         8 8888     ,88   8 8888       . `88888.       \n" +
                "     .8. `88888.       `8.`8888.        8 8888       8 888888888888 8 8888.   ,88'   8 8888      .8. `88888.      \n" +
                "    .8`8. `88888.       `8.`8888.       8 8888       8 8888         8 888888888P'    8 8888     .8`8. `88888.     \n" +
                "   .8' `8. `88888.       `8.`8888.      8 8888       8 8888         8 8888`8b        8 8888    .8' `8. `88888.    \n" +
                "  .8'   `8. `88888.  8b   `8.`8888.     8 8888       8 8888         8 8888 `8b.      8 8888   .8'   `8. `88888.   \n" +
                " .888888888. `88888. `8b.  ;8.`8888     8 8888       8 8888         8 8888   `8b.    8 8888  .888888888. `88888.  \n" +
                ".8'       `8. `88888. `Y8888P ,88P'     8 8888       8 888888888888 8 8888     `88.  8 8888 .8'       `8. `88888. \n");

        System.out.println("WELCOME TO ASTERIA TRADING SERVER LAUNCH");
        System.out.println("Documentation: https://bit.ly/3v6CTXk");
        System.out.println();
        System.out.println("Server information:");
    }

    /**
     * Sets up socket connections to clients
     * @throws Exception
     */
    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        ServerSocket serverSocket = new ServerSocket(4242);
        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connecting to..." + clientSocket);
            System.out.println();
            ClientHandler handler = new ClientHandler(this, clientSocket);
            //arraylist of all handlers, set name
            Thread t = new Thread(handler);
            t.start();

            clientList.add(handler);
            /*
            Update table on each new connection
             */
            auctionUpdates();
        }
    }

    /**
     * For each new connection the database refreshes information about current auctions
     * @return
     * @throws ParseException
     */
    private String auctionUpdates() throws ParseException {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();

        String lastPieceCmd = "SELECT piece_number, LAST_VALUE (piece_number) OVER( ORDER BY piece_number RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING ) FROM asteria.artwork";
        int lastPiece = intSQL(lastPieceCmd);
        String firstPieceCmd = "SELECT FIRST_VALUE(piece_number) over (ORDER BY piece_number) from asteria.artwork";
        int firstPiece = intSQL(firstPieceCmd);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);

        String result = "";
        String returnString = "";

        for(int i = firstPiece; i <= lastPiece ; i++) {
            String stringCmd = "SELECT end_time FROM asteria.artwork WHERE piece_number = " + i;
            try {
                Statement statement = connectDB.createStatement();
                ResultSet queryResult = statement.executeQuery(stringCmd);
                while(queryResult.next()) {
                    result = queryResult.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Date endTime = null;
            long seconds = 0;
            try {
                 endTime = dateFormat.parse(result);
                 seconds = (endTime.getTime() - currentTime.getTime()) / 1000;
            }catch (Exception e) {
                
            }


            String booleanCmd = "SELECT onAuction FROM asteria.artwork WHERE piece_number = " + i;
            String booleanResult = "";
            try {
                Statement statement = connectDB.createStatement();
                ResultSet queryResult = statement.executeQuery(booleanCmd);
                while(queryResult.next()) {
                    booleanResult = queryResult.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if(seconds <= 0 && booleanResult.equals("TRUE")) {
                //System.out.println(seconds);

                String onAuctionCmd = "UPDATE asteria.artwork SET onAuction = 'FALSE' WHERE piece_number = " + i;
                try {
                    Statement statement = connectDB.createStatement();
                    statement.executeUpdate(onAuctionCmd);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String pieceCmd = "SELECT artwork_title FROM asteria.artwork WHERE piece_number = " + i;
                String pieceName = "";
                try {
                    Statement statement = connectDB.createStatement();
                    ResultSet queryResult = statement.executeQuery(pieceCmd);
                    while(queryResult.next()) {
                        pieceName = queryResult.getString(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String highestBidder = setNewOwner(i);
                returnString = returnString + "[SOLD] " + highestBidder + " won auction on " + pieceName + "\n";
                updateHistory(returnString);

            }
        }
        return returnString;
    }

    /**
     * Updates server history
     * @param returnString
     */
    public void updateHistory(String returnString) {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();

        String currentRecentlySold = "SELECT recently_sold FROM asteria.transactions";
        String recentlysold = "";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(currentRecentlySold);
            while(queryResult.next()) {
                recentlysold = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String onAuctionCmd = "UPDATE asteria.transactions SET recently_sold = '" +  (returnString + recentlysold) + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(onAuctionCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String serverHistoryCmd = "SELECT server_history FROM asteria.transactions";
        String serverHistory = "";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(serverHistoryCmd);
            while(queryResult.next()) {
                serverHistory = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String historyCmd = "UPDATE asteria.transactions SET server_history = '" +  (returnString + serverHistory) + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(historyCmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the new owner of a piece of artwork
     * @param piece_number
     * @return
     */
    private String setNewOwner(int piece_number) {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();
        String highestBidderCmd = "SELECT current_highest_bidder FROM asteria.artwork WHERE piece_number = " + piece_number;
        String result = "";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(highestBidderCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(result == null) {
            return null;
        }
        payment(result, piece_number);

        String setOwner = "UPDATE asteria.artwork SET owner = '" + result +"' WHERE piece_number = " + piece_number;
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setOwner);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Manages payments between clients
     * @param highestBidder
     * @param piece_number
     */
    private void payment(String highestBidder, int piece_number) {
        UploadDatabase connectNow = new UploadDatabase();
        Connection connectDB = connectNow.getConnection();

        String bidPriceCmd = "SELECT user_funds FROM asteria.login WHERE username = '" + highestBidder + "'";
        int buyerFunds  = intSQL(bidPriceCmd);

        String currentOwnerCmd = "SELECT owner FROM asteria.artwork WHERE piece_number = " + piece_number;
        String result = "";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(currentOwnerCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String bidPrice2Cmd = "SELECT user_funds FROM asteria.login WHERE username = '" + result + "'";
        int ownerFunds  = intSQL(bidPrice2Cmd);


        String piecePriceCmd = "SELECT bid_price FROM asteria.artwork WHERE piece_number = " + piece_number;
        int piecePrice  = intSQL(piecePriceCmd);

        ownerFunds += piecePrice;
        buyerFunds -= piecePrice;

       /* System.out.println(ownerFunds);
        System.out.println(buyerFunds);*/

        String setOwner = "UPDATE asteria.login SET user_funds = " + ownerFunds +" WHERE username = '" + result + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setOwner);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String setBuyer = "UPDATE asteria.login SET user_funds = " + buyerFunds +" WHERE username = '" + highestBidder + "'";
        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(setBuyer);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Abstract method used to get a double or integer from server
     * @param cmd
     * @return
     */
    public static int intSQL(String cmd) {
        int result = 0;
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

    /**
     * Client handler method to determine request from client
     * @param input
     * @return
     */
    protected String processRequest(String input) {
        Gson gson = new Gson();
        UploadMessage message = gson.fromJson(input, UploadMessage.class);

        System.out.println(message);
        try {
            switch(message.type) {
                case "upload":
                    ServerUpload upload = new ServerUpload(input);
                    return "Upload Complete";
                case "auctionRequest":
                    Runnable runnable = new BidUpload(input);
                    Thread thread = new Thread(runnable);
                    thread.start();
                    //thread.join();
                    return "Auction information updated";
                case  "refresh":
                    auctionUpdates();
                    System.out.println("Server Refreshed.");
                    return "Auction Updated";
                case "purchase":
                    Purchase purchase = new Purchase(message);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

}