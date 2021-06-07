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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Updates the amount of funds a user has to bid or purchase
 */
public class WalletController implements Initializable {
    @FXML
    private Label fullnameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button addFundsButton;

    @FXML
    private TextField fundsBox;

    @FXML
    private ImageView refreshAnimation;

    @FXML
    private Label invalidUSDLabel;

    @FXML
    private Label currentFundsLabel;

    @FXML
    private Label currentBidLabel;

    @FXML
    private Label ownedLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPage();
    }

    public void minimizeWindow(javafx.scene.input.MouseEvent mouseEvent) {
        AuctionClient.minimizeWindow();
    }
    public void clickExit(javafx.scene.input.MouseEvent mouseEvent) {
        System.exit(0);
    }
    public void homeClick(javafx.scene.input.MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.mainScreen);
    }
    public void uploadClick(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        AuctionClient.newUpload();
    }
    public void galleryClick(javafx.scene.input.MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.gallery);
    }
    public void exploreClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.exploreTemplate);

    }

    public void refresh(MouseEvent mouseEvent) {

        Thread rt = new Thread() {
            @Override
            public void run() {
                RotateTransition rt = new RotateTransition(Duration.seconds(5), refreshAnimation);
                rt.setByAngle(360);
                rt.play();
            }
        };
        invalidUSDLabel.setText(" ");
        loadPage();

        rt.start();

    }

    public void fundsClick(MouseEvent mouseEvent) {
        //set funds UPDATE SQL
        String newFunds = fundsBox.getText();
        if(newFunds.isEmpty()) {
            invalidUSDLabel.setText("Enter USD to add to wallet.");
        }
        else if(!isDouble(newFunds)) {
            invalidUSDLabel.setText("Please enter valid USD.");
        }
        else {
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getConnection();
            String cmd = "UPDATE asteria.login SET user_funds = " + (currentUSD + Double.parseDouble(newFunds)) + " WHERE username = '" + Params.USERNAME +"'";
            try {
                Statement statement = connectDB.createStatement();
                statement.executeUpdate(cmd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            invalidUSDLabel.setText("Funds updated. Please refresh.");
            fundsBox.clear();
        }
    }
    static boolean isDouble(String s) {
        try {
            double i = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public String username;
    public String fullname;
    int numBids;
    double currentUSD;
    int numOwned;

    public void loadPage() {
        this.username = Params.USERNAME;
        usernameLabel.setText(username);

        String firstName = SQLString("firstname");
        String lastName = SQLString("lastname");
        this.fullname = firstName.toUpperCase(Locale.ROOT) + " " + lastName.toUpperCase(Locale.ROOT);
        fullnameLabel.setText(fullname);

        this.currentUSD = intSQL("user_funds");
        Params.USER_FUNDS = currentUSD;
        //System.out.println(Params.USER_FUNDS);
        currentFundsLabel.setText("$" + currentUSD);

        this.numBids = artworkSQL("current_highest_bidder");
        String numBidStr = String.valueOf(numBids) + " current bids";
        currentBidLabel.setText(numBidStr);

        this.numOwned = artworkSQL("owner");
        ownedLabel.setText(String.valueOf(numOwned));

    }

    public static int artworkSQL(String request) {
        int result = 0;
        String cmd = "SELECT COUNT(" + request + ") AS result FROM asteria.artwork WHERE "+ request + " = '" + Params.USERNAME + "'";

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

    public static int intSQL(String request) {
        int result = 0;
        String intCmd = "SELECT " + request + " FROM asteria.login WHERE username = '" + Params.USERNAME + "'";

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(intCmd);
            while(queryResult.next()) {
                result = queryResult.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String SQLString(String request) {
        String result = "";
        String stringCmd = "SELECT " + request + " FROM asteria.login WHERE username = '" + Params.USERNAME + "'";

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(stringCmd);
            while(queryResult.next()) {
                result = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
