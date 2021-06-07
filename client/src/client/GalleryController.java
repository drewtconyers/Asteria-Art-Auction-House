/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package client;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Loads in all artwork the current user owns
 */
public class GalleryController implements Initializable {
    public void minimizeWindow (MouseEvent mouseEvent) {
        AuctionClient.minimizeWindow();
    }
    public void clickExit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void homeClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.mainScreen);
    }
    public void exploreClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.exploreTemplate);
    }
    public void uploadButton(MouseEvent mouseEvent) throws IOException {
        AuctionClient.newUpload();

    }
    public void walletClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.wallet);
    }

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView refreshAnimation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadUserPieces();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void refresh(MouseEvent event) throws IOException {
        Thread rt = new Thread() {
            @Override
            public void run() {
                RotateTransition rt = new RotateTransition(Duration.seconds(5), refreshAnimation);
                rt.setByAngle(360);
                rt.play();
            }
        };
        loadUserPieces();

        rt.start();
    }

    public void loadUserPieces() throws IOException {
        gridPaneInit();
    }

    public void gridPaneInit() throws IOException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String numPiecesCmd = "SELECT COUNT(owner) AS owner FROM asteria.artwork  WHERE owner = '" + Params.USERNAME + "'";
        int numberOfPieces = 0;
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(numPiecesCmd);
            while(queryResult.next()) {
                numberOfPieces = queryResult.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(numberOfPieces <= 0) {
            return;
        }

        String ownerCmd = "SELECT piece_number FROM asteria.artwork  WHERE owner = '" + Params.USERNAME + "'";
        ArrayList<Integer> pieces = new ArrayList<Integer>();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(ownerCmd);
            while(queryResult.next()) {
                int result = queryResult.getInt(1);
                pieces.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int numRows = ((numberOfPieces % 3) == 0) ? (numberOfPieces / 3) : getNumRows(numberOfPieces);
        int gridHeight = numRows * 300;
        gridPane.getRowConstraints().clear();
        gridPane.getColumnConstraints().clear();
        anchorPane.setPrefHeight(gridHeight);
        anchorPane.setPrefWidth(900);
        gridPane.setPrefWidth(900);
        gridPane.setPrefHeight(gridHeight);


        for(int i = 0; i <= 2; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHalignment(HPos.CENTER);
            colConst.setPrefWidth(300.0);

            gridPane.getColumnConstraints().add(colConst);
        }
        for(int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setValignment(VPos.CENTER);
            rowConst.setPrefHeight(300);
            gridPane.getRowConstraints().add(rowConst);
        }


        int loc = 0;
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < 3; j++) {
                if(loc >= numberOfPieces) {
                    return;
                }
                ImageView gridImage = new ImageView();
                String encodedString = getDecodedImage((Integer) pieces.get(loc));

                BASE64Decoder base64Decoder = new BASE64Decoder();
                ByteArrayInputStream in = new ByteArrayInputStream(base64Decoder.decodeBuffer(encodedString));
                Image image = new Image(in);
                gridImage.setImage(image);
                gridImage.setFitHeight(300);
                gridImage.setFitWidth(300);
                gridImage.setPreserveRatio(true);
                gridPane.add(gridImage, j, i);
                loc++;

            }
        }

    }

    public static String getDecodedImage(int val) {
        String result = "";
        //System.out.println(val);
        String imgCmd = "SELECT encoded_image FROM asteria.artwork WHERE piece_number = " + val;


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

    private int getNumRows(int numberOfPieces) {
        int result = 0;
        if(numberOfPieces % 3 == 1) {
            result = (numberOfPieces - 1) / 3 + 1;

        }

        else if(numberOfPieces % 3 == 2) {
            result = (numberOfPieces - 2) / 3 + 1;
        }
        return result;
    }

}
