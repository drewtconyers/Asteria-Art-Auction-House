/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package client;

/**
 * UploadMessage sent to Client
 */
public class UploadMessage {
    String type;
    String artworkTitle;
    String owner;
    String encodedImage;
    String details;
    String bidderName;
    String bidHistory;
    String username;

    int hour;
    int minute;
    int piece_number;

    double bidUSD;
    double startingBid;
    double purchasePrice;
    double user_funds;

    /*
    For refresh
     */
    public UploadMessage(String type) {
        this.type = type;
    }

    /*
    For purchase
     */
    public UploadMessage(String type, String username, double purchasePrice, int piece_number, double user_funds, String owner){
        this.type = type;
        this.username = username;
        this.purchasePrice = purchasePrice;
        this.piece_number = piece_number;
        this.user_funds = user_funds;
        this.owner = owner;
        System.out.println("client-side message created");
    }

    /*
    For bid
     */
    public UploadMessage(String type, String bidderName, String artworkTitle, double bidUSD, int piece_number, String bidHistory) {
        this.type = type;
        this.bidderName = bidderName;
        this.artworkTitle = artworkTitle;
        this.bidUSD = bidUSD;
        this.piece_number = piece_number;
        this.bidHistory = bidHistory;
        System.out.println("client-side message created");
    }

    /*
    For artwork upload
     */
    public UploadMessage(String artworkTitle, String owner, String encodedImage, String details, double startingBid, double purchasePrice, String bidHistory, int hour, int minute) {
        this.type = "upload";
        this.artworkTitle = artworkTitle;
        this.owner = owner;
        this.encodedImage = encodedImage;
        this.details = details;
        this.startingBid = startingBid;
        this.purchasePrice = purchasePrice;
        this.bidHistory = bidHistory;
        this.hour = hour;
        this.minute = minute;
        System.out.println("client-side message created");
    }

}
