/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */

package client;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.*;

/**
 * Client login screen
 */
public class Login {

    @FXML
    private TextField usernameBox;

    @FXML
    private PasswordField passwordBox;

    @FXML
    private Label loginMessageLabel;

    public void minimizeWindow(MouseEvent mouseEvent) {
        AuctionClient.minimizeWindow();
    }
    public void clickExit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void loginButton(MouseEvent mouseEvent) {
        if(!usernameBox.getText().trim().isEmpty() && !passwordBox.getText().trim().isEmpty()) {
            validateLogin();
        }
        else if (usernameBox.getText().trim().isEmpty() && passwordBox.getText().trim().isEmpty()) {
            loginMessageLabel.setText("Enter username and password.");
        }
        else if (usernameBox.getText().trim().isEmpty()){
            loginMessageLabel.setText("Enter username.");
        }
        else if (passwordBox.getText().trim().isEmpty()) {
            loginMessageLabel.setText("Enter password.");
        }

    }

    public void validateLogin(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        int hashPassword = hashUserPassword(passwordBox.getText());
        String verifyLogin = "SELECT count(1) FROM asteria.login WHERE username = '" + usernameBox.getText() + "' AND hashPassword ='" + hashPassword + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()) {
                if(queryResult.getInt(1) == 1) {
                    loginMessageLabel.setText("Login complete.");
                    Params.setUSERNAME(usernameBox.getText());
                    AuctionClient.wallet = FXMLLoader.load(getClass().getResource("clientFXMLs/asteriaWallet.fxml"));
                    AuctionClient.gallery = FXMLLoader.load(getClass().getResource("clientFXMLs/galleryTemplate.fxml"));
                    AuctionClient.userLogin(AuctionClient.mainScreen);
                }
                else {
                    loginMessageLabel.setText("Invalid login. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerButton(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.register);
    }

    private int hashUserPassword(String password) {
        int hashPassword = password.hashCode();

        return hashPassword;
    }

    @FXML
    void guestUser(MouseEvent event) throws IOException {
        loginMessageLabel.setText("Login complete.");
        Params.setUSERNAME("GUEST");
        AuctionClient.wallet = FXMLLoader.load(getClass().getResource("clientFXMLs/asteriaWallet.fxml"));
        AuctionClient.gallery = FXMLLoader.load(getClass().getResource("clientFXMLs/galleryTemplate.fxml"));
        AuctionClient.userLogin(AuctionClient.mainScreen);
    }

}
