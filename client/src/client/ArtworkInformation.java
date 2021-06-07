/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package client;

/**
 * Message sent to server for new bid
 */
public class ArtworkInformation {
    String artworkTitle;
    String owner;
    String encodedImage;
    String details;
    double startingBid;
    double purchasePrice;
    String bidHistory;
    int hour;
    int minute;

    int piece_number;
    public ArtworkInformation(int piece_number){
        this.piece_number = piece_number;
    }
    public ArtworkInformation(String artworkTitle, String owner, String encodedImage, String details, double startingBid, double purchasePrice, String bidHistory, int hour, int minute) {
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
