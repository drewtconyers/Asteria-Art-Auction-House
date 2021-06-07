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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Registering client to database
 */
public class Register {

    @FXML
    private TextField usernameBox;

    @FXML
    private PasswordField passwordBox;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField firstNameBox;

    @FXML
    private TextField lastnameBox;

    @FXML
    private Label backToLogin;

    public void clickExit(MouseEvent mouseEvent) {
        System.exit(0);
    }
    public void minimizeWindow(MouseEvent mouseEvent) {
        AuctionClient.minimizeWindow();
    }

    public void backToLogin(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.login);
    }

    public void registerButton(MouseEvent mouseEvent) throws IOException {
        if(!usernameBox.getText().trim().isEmpty() && !passwordBox.getText().trim().isEmpty() && !firstNameBox.getText().trim().isEmpty() && !lastnameBox.getText().trim().isEmpty()) {
            registerUser();
        }
        else if (usernameBox.getText().trim().isEmpty() && passwordBox.getText().trim().isEmpty() && firstNameBox.getText().trim().isEmpty() && lastnameBox.getText().trim().isEmpty()) {
            loginMessageLabel.setText("Please enter your information.");
        }
        else if (usernameBox.getText().trim().isEmpty()){
            loginMessageLabel.setText("Enter username.");
        }
        else if (passwordBox.getText().trim().isEmpty()) {
            loginMessageLabel.setText("Enter password.");
        }
        else if (firstNameBox.getText().trim().isEmpty()) {
            loginMessageLabel.setText("Enter your first name.");
        }
        else if (firstNameBox.getText().trim().isEmpty()) {
            loginMessageLabel.setText("Enter your last name.");
        }
    }

    public void registerUser() throws IOException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        int hashPassword = hashUserPassword(passwordBox.getText());

        String registerNewUser = "INSERT INTO asteria.login (firstname, lastname, username, hashPassword, user_funds) VALUES ('" + firstNameBox.getText() + "', '" + lastnameBox.getText() +"', '" + usernameBox.getText() + "', " + hashPassword + ", " + 0.00 +  ")";
        String checkUsernames = "SELECT count(1) FROM asteria.login WHERE username = '" + usernameBox.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(checkUsernames);

            while(queryResult.next()) {
                if(queryResult.getInt(1) == 1) {
                    loginMessageLabel.setText("Username already taken. Enter new username.");
                    return;
                }
            }

            Statement createStatement = connectDB.createStatement();
            createStatement.executeUpdate(registerNewUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginMessageLabel.setText("Registration complete.");
        Params.setUSERNAME(usernameBox.getText());
        AuctionClient.wallet = FXMLLoader.load(getClass().getResource("clientFXMLs/asteriaWallet.fxml"));
        AuctionClient.gallery = FXMLLoader.load(getClass().getResource("clientFXMLs/galleryTemplate.fxml"));
        AuctionClient.userLogin(AuctionClient.mainScreen);
    }

    private int hashUserPassword(String password) {
        int hashPassword = password.hashCode();

        return hashPassword;
    }
}
