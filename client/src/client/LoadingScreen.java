/*
 *  EE422C Final Project submission by
 *  Drew Conyers
 *  dtc888
 *  17115
 *  Spring 2021
 */
package client;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Preloader for application
 */
public class LoadingScreen extends Preloader {
    static Stage window;
    Parent loading;

    @FXML
    private ImageView loadingGif;

    @FXML
    private Circle whiteCircle;

    @FXML
    private Circle seaCircle;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        loading = FXMLLoader.load(getClass().getResource("clientFXMLs/loading.fxml"));
        window.getIcons().add(new Image("client/images/logo-green.png"));

        Scene startingScene = new Scene(loading);
        window.setScene(startingScene);
        window.initStyle(StageStyle.UNDECORATED);
        window.show();
    }
}
