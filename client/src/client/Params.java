/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package client;

/**
 * Parameters for server
 */
public class Params {
    public static int PORT = 4242;
    public static String HOST = "localhost";
    public static String USERNAME = "";
    public static int PIECE_NUMBER_REQUEST;
    public static double USER_FUNDS;

    public static void setUSERNAME(String USERNAME) {
        Params.USERNAME = USERNAME;
    }

    public static void setPieceNumberRequest(int PIECE_NUMBER_REQUEST) {
        Params.PIECE_NUMBER_REQUEST = PIECE_NUMBER_REQUEST;
    }
}
