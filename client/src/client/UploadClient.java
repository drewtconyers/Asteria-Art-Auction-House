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

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * UploadClient
 */
public class UploadClient {
    /*
    NETWORKING SETUP for Upload Controller Client
     */
    private BufferedReader fromServer;
    private PrintWriter toServer;

    public UploadClient() {
        try {
            setUpNetWorking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpNetWorking() throws Exception {
        Socket socket = new Socket(Params.HOST, Params.PORT);
        System.out.println("New upload client connection to... " + socket);

        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new PrintWriter(socket.getOutputStream());

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String input;
                try{
                    while((input = fromServer.readLine()) != null) {
                        System.out.println("From server: " + input);
                        System.out.println();
                        processRequest();
                    }
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });



        readerThread.start();
    }

    protected void processRequest() {
        return;
    }

    protected void sendToServer(String string) {
        System.out.println("Sending to server " + Params.USERNAME + "'s new artwork: " + artworkName.getText());
        toServer.println(string);
        toServer.flush();
    }

    public void uploadArt(String artworkTitle, String owner, String encodedImage, String details, double startingBid, double purchasePrice, String bidHistory, int hour, int minute) {
        UploadMessage request = new UploadMessage(artworkTitle, owner, encodedImage, details, startingBid, purchasePrice, bidHistory, hour, minute);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        sendToServer(gson.toJson(request));
    }

    /*
    GUI for Upload Controller Client
     */
    public void minimizeWindow(MouseEvent mouseEvent) {
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
    public void galleryClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.gallery);
    }
    public void uploadButton(MouseEvent mouseEvent) throws IOException {
        AuctionClient.newUpload();
    }

    @FXML
    private TextField artworkName;

    @FXML
    private TextArea artworkDescription;

    @FXML
    private TextField purchasePrice;

    @FXML
    private TextField hourTime;

    @FXML
    private TextField minuteTime;

    @FXML
    private TextField bidPrice;

    @FXML
    private Label requiredMessageLabel;

    @FXML
    private Label uploadCompleteLabel;

    @FXML
    private Button uploadArtButton;

    File selectedFile;
    String encodedString;
    public void fileUploadButton(MouseEvent mouseEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select media to upload");
        fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("Image Files","*.png", "*.jpg", "*.gif","*.jfif")
        );
        selectedFile = fileChooser.showOpenDialog(AuctionClient.window);

        if(selectedFile != null) {
            uploadCompleteLabel.setText("Upload complete.");
            encodedString = fileToBase64StringConversion(selectedFile);
        }
    }

    public String fileToBase64StringConversion(File inputFile) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public void finalUploadButton(MouseEvent mouseEvent) {
        if(artworkName.getText().isEmpty() || (selectedFile == null) || bidPrice.getText().isEmpty() || hourTime.getText().isEmpty() || minuteTime.getText().isEmpty()) {
            requiredMessageLabel.setText("Please enter all required fields.");
        }
        else if(!isDouble(bidPrice.getText()) && purchasePrice.getText().isEmpty()) {
            requiredMessageLabel.setText("For starting bid price enter valid USD value.");
        }
        else if(!isDouble(bidPrice.getText()) || !isDouble(purchasePrice.getText()) && !purchasePrice.getText().isEmpty()) {
            requiredMessageLabel.setText("For starting bid and purchase price enter valid USD value.");
        }
        else if(!isInt(hourTime.getText()) || !isInt(minuteTime.getText())) {
            requiredMessageLabel.setText("For auction time please enter valid integers.");
        }
        else if(Double.parseDouble(bidPrice.getText()) <= 0) {
            requiredMessageLabel.setText("Set starting price greater than $0");
        }
        //may need limit for minutes entry < 60
        else if(!artworkName.getText().isEmpty() && (selectedFile != null) && !bidPrice.getText().isEmpty() && !hourTime.getText().isEmpty() && !minuteTime.getText().isEmpty()) {

            //send to server
            String bidPriceString = bidPrice.getText();
            double bidPriceDouble = Double.parseDouble(bidPriceString);
            double purchasePriceDouble = 0;
            if(!purchasePrice.getText().isEmpty()){
                String purchasePriceString = purchasePrice.getText();
                purchasePriceDouble = Double.parseDouble(purchasePriceString);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
            Date date = new Date();

            String bidHistory = "Originally put on auction by " + Params.USERNAME + " at " + dateFormat.format(date);

            String hourString = hourTime.getText();
            int hourInt = Integer.parseInt(hourString);
            String minuteString = minuteTime.getText();
            int minuteInt = Integer.parseInt(minuteString);

            String details = (artworkDescription.getText().isEmpty()) ? "No available information about this piece." : artworkDescription.getText();

            uploadArt(artworkName.getText(), Params.USERNAME, encodedString, details, bidPriceDouble, purchasePriceDouble, bidHistory, hourInt, minuteInt);

            pageRefresh();
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

    static boolean isInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void pageRefresh() {
        requiredMessageLabel.setText("Artwork now on auction.");
        FadeTransition fade = new FadeTransition(Duration.millis(6000), requiredMessageLabel);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
        artworkName.clear();
        selectedFile = null;
        uploadCompleteLabel.setText(" ");
        artworkDescription.clear();
        bidPrice.clear();
        purchasePrice.clear();
        hourTime.clear();
        minuteTime.clear();


    }

    public void walletClick(MouseEvent mouseEvent) {
        AuctionClient.swapScenes(AuctionClient.wallet);
    }
}
