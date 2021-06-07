/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package server;

/**
 * Upload Message used for GSON communication between client and server
 */
public class UploadMessage {
    String type;
    String artworkTitle;
    String owner;
    String encodedImage;
    String details;
    String bidHistory;
    String bidderName;
    String username;

    double startingBid;
    double purchasePrice;
    double user_funds;
    double bidUSD;

    int hour;
    int minute;
    int piece_number;


    public UploadMessage(String type) {
    }

    /**
     * Server/Client Message for bid on piece
     * @param type
     * @param bidderName
     * @param artworkTitle
     * @param bidUSD
     * @param piece_number
     * @param bidHistory
     */
    public UploadMessage(String type, String bidderName, String artworkTitle, double bidUSD, int piece_number, String bidHistory) {
        this.type = type;
        this.bidderName = bidderName;
        this.artworkTitle = artworkTitle;
        this.bidUSD = bidUSD;
        this.piece_number = piece_number;
        this.bidHistory = bidHistory;

    }

    /**
     * Server/Client Message for purchase of piece
     * @param type
     * @param username
     * @param purchasePrice
     * @param piece_number
     * @param user_funds
     * @param owner
     */
    public UploadMessage(String type, String username, double purchasePrice, int piece_number, double user_funds, String owner){
        this.type = type;
        this.username = username;
        this.purchasePrice = purchasePrice;
        this.piece_number = piece_number;
        this.user_funds = user_funds;
        this.owner = owner;
    }

    /**
     * Server/Client Message for new upload to database
     * @param artworkTitle
     * @param owner
     * @param encodedImage
     * @param details
     * @param startingBid
     * @param purchasePrice
     * @param bidHistory
     * @param hour
     * @param minute
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
    }
}
