/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class MainScreen implements Initializable {

    @FXML
    private ImageView newArt1;

    @FXML
    private ImageView newArt2;

    @FXML
    private ImageView newArt3;

    @FXML
    private ImageView newArt4;

    @FXML
    private ImageView newArt5;

    @FXML
    private ImageView newArt6;

    @FXML
    private ImageView newArt7;

    @FXML
    private ImageView refreshAnimation;

    @FXML
    private Label recentlySold;

    @FXML
    private Label serverHistory;

    public void clickExit(MouseEvent mouseEvent) {
        System.exit(0);
    }
    public void minimizeWindow(MouseEvent mouseEvent) {
        AuctionClient.minimizeWindow();
    }

    public void uploadButton(MouseEvent mouseEvent) throws IOException {
        AuctionClient.newUpload();
    }
    public void exploreClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.exploreTemplate);
    }
    public void galleryClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.gallery);
    }
    public void walletClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.wallet);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            newArt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recentlySold();
        serverHistory();
    }

    @FXML
    void img1Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(0).piece_number);
        loadAuctionPage();
       // System.out.println(newArtInformation.get(0).piece_number + " " + newArtInformation.get(1).piece_number);
    }

    @FXML
    void img2Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(1).piece_number);
        //System.out.println(newArtInformation.get(1).piece_number);
        loadAuctionPage();
    }

    @FXML
    void img3Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(2).piece_number);
        loadAuctionPage();
    }

    @FXML
    void img4Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(3).piece_number);
        loadAuctionPage();
    }

    @FXML
    void img5Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(4).piece_number);
        loadAuctionPage();
    }

    @FXML
    void img6Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(5).piece_number);
        loadAuctionPage();
    }

    @FXML
    void img7Click(MouseEvent event) throws IOException {
        Params.setPieceNumberRequest(newArtInformation.get(6).piece_number);
        loadAuctionPage();
    }

    public void loadAuctionPage() throws IOException {
        AuctionClient.loadArtworkAuctionPage();
    }

    public void refresh(MouseEvent mouseEvent) throws IOException {

        Thread rt = new Thread() {
            @Override
            public void run() {
                RotateTransition rt = new RotateTransition(Duration.seconds(5), refreshAnimation);
                rt.setByAngle(360);
                rt.play();
            }
        };

        Thread refresh = new Thread() {
            @Override
            public void run() {
                try {
                    newArt();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        refresh.start();
        rt.start();
        recentlySold();
        serverHistory();
    }

    int lastPiece;
    int firstPiece;
    ArrayList<ArtworkInformation> newArtInformation;

    public void newArt() throws IOException {
        newArtInformation = new ArrayList<ArtworkInformation>();

        String lastPieceCmd = "SELECT piece_number, LAST_VALUE (piece_number) OVER( ORDER BY piece_number RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING ) FROM asteria.artwork";
        int lastPiece = intSQL(lastPieceCmd);
        this.lastPiece = lastPiece;
        String firstPieceCmd = "SELECT FIRST_VALUE(piece_number) over (ORDER BY piece_number) from asteria.artwork";
        int firstPiece = intSQL(firstPieceCmd);
        this.firstPiece = firstPiece;

        int loopCounter = 1;
        while(lastPiece >= firstPiece && loopCounter != 9) {
            String encodedString = getDecodedImage(lastPiece);
            switch (loopCounter) {
                case 1 :
                    ArtworkInformation img1 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img1);

                    BASE64Decoder base64Decoder = new BASE64Decoder();
                    ByteArrayInputStream in = new ByteArrayInputStream(base64Decoder.decodeBuffer(encodedString));
                    newArt1.setPreserveRatio(true);
                    Image image = new Image(in);
                    newArt1.setImage(image);
                    break;
                case 2 :
                    ArtworkInformation img2 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img2);

                    BASE64Decoder base64Decoder2 = new BASE64Decoder();
                    ByteArrayInputStream in2 = new ByteArrayInputStream(base64Decoder2.decodeBuffer(encodedString));
                    newArt2.setPreserveRatio(true);
                    Image image2 = new Image(in2);
                    newArt2.setImage(image2);
                    break;
                case 3 :
                    ArtworkInformation img3 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img3);

                    BASE64Decoder base64Decoder3 = new BASE64Decoder();
                    ByteArrayInputStream in3 = new ByteArrayInputStream(base64Decoder3.decodeBuffer(encodedString));
                    newArt3.setPreserveRatio(true);
                    Image image3 = new Image(in3);
                    newArt3.setImage(image3);
                    break;
                case 4 :
                    ArtworkInformation img4 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img4);

                    BASE64Decoder base64Decoder4 = new BASE64Decoder();
                    ByteArrayInputStream in4 = new ByteArrayInputStream(base64Decoder4.decodeBuffer(encodedString));
                    newArt4.setPreserveRatio(true);
                    Image image4 = new Image(in4);
                    newArt4.setImage(image4);
                    break;
                case 5 :
                    ArtworkInformation img5 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img5);

                    BASE64Decoder base64Decoder5 = new BASE64Decoder();
                    ByteArrayInputStream in5 = new ByteArrayInputStream(base64Decoder5.decodeBuffer(encodedString));
                    newArt5.setPreserveRatio(true);
                    Image image5 = new Image(in5);
                    newArt5.setImage(image5);
                    break;
                case 6 :
                    ArtworkInformation img6 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img6);

                    BASE64Decoder base64Decoder6 = new BASE64Decoder();
                    ByteArrayInputStream in6 = new ByteArrayInputStream(base64Decoder6.decodeBuffer(encodedString));
                    newArt6.setPreserveRatio(true);
                    Image image6 = new Image(in6);
                    newArt6.setImage(image6);
                    break;
                case 7 :
                    ArtworkInformation img7 = new ArtworkInformation(lastPiece);
                    newArtInformation.add(img7);

                    BASE64Decoder base64Decoder7 = new BASE64Decoder();
                    ByteArrayInputStream in7 = new ByteArrayInputStream(base64Decoder7.decodeBuffer(encodedString));
                    newArt7.setPreserveRatio(true);
                    Image image7 = new Image(in7);
                    newArt7.setImage(image7);
                    break;
            }
            //System.out.println("Loop counter: " + loopCounter);
            loopCounter++;
            //System.out.println("lastpiece" + lastPiece);
            lastPiece--;
        }
    }

    public static String getDecodedImage(int val) {
        String result = "";
        String imgCmd = "SELECT encoded_image FROM asteria.artwork WHERE piece_number =" + val;

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(imgCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int intSQL(String cmd) {
        int result = 0;
        DatabaseConnection connectNow = new DatabaseConnection();
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

    public void recentlySold() {
        String result = "";
        String recentCmd = "SELECT recently_sold FROM asteria.transactions";

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(recentCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        recentlySold.setText(result);
    }

    public void serverHistory() {
        String result = "";
        String recentCmd = "SELECT server_history FROM asteria.transactions";

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(recentCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        serverHistory.setText(result);
    }

}
