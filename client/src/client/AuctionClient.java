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
import com.sun.javafx.application.LauncherImpl;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main method for program
 * Main client for communicating new bids to server. Displays artwork that is on auction UI.
 */
public class AuctionClient extends Application implements Initializable {
    public static Stage window;
    public static Parent login;
    public static Parent register;
    public static Parent mainScreen;
    public static Parent artworkAuctionPage;
    public static Parent uploadTemplate;
    public static Parent exploreTemplate;
    public static Parent gallery;
    public static Parent wallet;


    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("Asteria Trading");

        loadProgram();
        Scene startingScene = new Scene(login);
        window.setScene(startingScene);
        window.initStyle(StageStyle.UNDECORATED);
        LoadingScreen.window.close();
        window.show();

        window.getIcons().add(new Image("client/images/logo-green.png"));

        swapScenes(login);



    }

    public void loadProgram() throws IOException {
        login = FXMLLoader.load(getClass().getResource("clientFXMLs/login.fxml"));
        register = FXMLLoader.load(getClass().getResource("clientFXMLs/register.fxml"));
        mainScreen = FXMLLoader.load(getClass().getResource("clientFXMLs/main screen.fxml"));
        uploadTemplate = FXMLLoader.load(getClass().getResource("clientFXMLs/uploadTemplate.fxml"));
        exploreTemplate = FXMLLoader.load(getClass().getResource("clientFXMLs/exploreTemplate.fxml"));
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(AuctionClient.class, LoadingScreen.class, args);
    }


    public static void minimizeWindow(){window.setIconified(true);}
    public static void swapScenes(Parent newContent) {
        window.getScene().setRoot(newContent);
    }

    public void homeClick(MouseEvent mouseEvent) {
        swapScenes(mainScreen);
        try {
            socket.close();
        } catch (SocketException e) {
            System.out.println("Auction client disconnected.");
        } catch (IOException e) {
            System.out.println("Auction client disconnected.");
        }
    }
    public void exploreClick(MouseEvent mouseEvent)  {
        swapScenes(exploreTemplate);
        try {
            socket.close();
        } catch (SocketException e) {
            System.out.println("Auction client disconnected.");
        } catch (IOException e) {
            System.out.println("Auction client disconnected.");
        }
    }
    public void galleryClick(MouseEvent mouseEvent) {
        swapScenes(gallery);
        try {
            socket.close();
        } catch (SocketException e) {
            System.out.println("Auction client disconnected.");
        } catch (IOException e) {
            System.out.println("Auction client disconnected.");
        }
    }
    public void uploadClick(MouseEvent mouseEvent) {
        swapScenes(uploadTemplate);
        try {
            socket.close();
        } catch (SocketException e) {
            System.out.println("Auction client disconnected.");
        } catch (IOException e) {
            System.out.println("Auction client disconnected.");
        }
    }
    public void walletClick(MouseEvent mouseEvent) {
        swapScenes(AuctionClient.wallet);
    }

    public static void newUpload() {
        swapScenes(uploadTemplate);
    }
    public static void userLogin(Parent newContent) {
        window.close();
        //get clients information
        Scene mainScene = new Scene(newContent);
        window.setScene(mainScene);
        window.show();
    }

    /*
    NETWORKING SETUP for Upload Controller Client
     */
    private BufferedReader fromServer;
    private PrintWriter toServer;
    Socket socket;

    private void setUpNetWorking() throws Exception {
        socket = new Socket(Params.HOST, Params.PORT);
        System.out.println("New auction client connection to... " + socket);

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
                    System.out.println("Auction client disconnected.");
                }
            }
        });
        readerThread.start();
    }

    protected void processRequest() {
        return;
    }

    protected void sendToServer(String string) {
        System.out.println("Sending to server " + Params.USERNAME + "'s new bid on: " + artworkTitle);
        toServer.println(string);
        toServer.flush();
    }

    /*
    Artwork Auction Page
     */

    String encodedImage;
    String owner;
    String artworkTitle;
    String details;
    String tradingHistory;
    String currentHighestBidder;
    String labelTime;

    boolean canPurchase;
    boolean auctionTimeout;

    int numberOfBidders;
    int piece_number;

    long seconds = 0;

    double currentBidPrice;
    double purchasePrice;

    Thread remainingTime;

    @FXML
    private ImageView artwork;

    @FXML
    private Label artworkOwner;

    @FXML
    private Label artworkTitleLabel;

    @FXML
    private Label purchasePriceLabel;

    @FXML
    private ImageView refreshAnimation;

    @FXML
    private Label detailsLabel;

    @FXML
    private Label tradingHistoryLabel;

    @FXML
    private Label currentBidLabel;

    @FXML
    private Label invalidBidLabel;

    @FXML
    private Label currentHighestBidderLabel;

    @FXML
    private Label bidsLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label invalidPurchaseLabel;

    @FXML
    private TextField bidBox;

    public static void loadArtworkAuctionPage() throws IOException {
        artworkAuctionPage = FXMLLoader.load(AuctionClient.class.getResource("clientFXMLs/artworkPageTemplate.fxml"));
        swapScenes(artworkAuctionPage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.auctionTimeout = false;

        try {
            setUpNetWorking();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            setImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            setText();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            remainingTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        bidPrice();
/*        while(auctionTimeout) {

        }*/
        //setup thread to watch for sql change
        //listen for variable change
        //https://stackoverflow.com/questions/8247926/java-event-listener-to-detect-a-variable-change

    }

    @FXML
    void bidButton(MouseEvent event) {
        String newFunds = bidBox.getText();
        if(auctionTimeout) {
            invalidBidLabel.setText("No longer available for auction.");
        }
        else if(newFunds.isEmpty()) {
            invalidBidLabel.setText("Enter bid.");
        }
        else if(!isDouble(newFunds)) {
            invalidBidLabel.setText("Please enter valid bid in USD.");
        }
        else if(Params.USER_FUNDS < Double.parseDouble(newFunds)) {
            invalidBidLabel.setText("Not enough funds to bid on this piece.");
        }
        else if(isDouble(newFunds) && Double.parseDouble(newFunds) <= currentBidPrice) {
            invalidBidLabel.setText("Entered bid is less than current price.");
        }
        else {
            newBid(Double.parseDouble(newFunds));
            bidBox.clear();
            invalidBidLabel.setText("Bid processed. Refresh page.");
        }
    }

    private void newBid(double bidPrice) {
        UploadMessage request = new UploadMessage("auctionRequest", Params.USERNAME, artworkTitle, bidPrice, Params.PIECE_NUMBER_REQUEST, tradingHistory);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //System.out.println(request.piece_number);
        sendToServer(gson.toJson(request));
    }

    static boolean isDouble(String s) {
        try {
            double i = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void bidPrice() {
        this.currentBidPrice = intSQL("bid_price");
        currentBidLabel.setText("$" + currentBidPrice);
    }

    public void setImage() throws IOException {
        String imgCmd = "SELECT encoded_image FROM asteria.artwork WHERE piece_number =" + Params.PIECE_NUMBER_REQUEST;

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(imgCmd);
            while(queryResult.next()) {
                this.encodedImage = queryResult.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BASE64Decoder base64Decoder5 = new BASE64Decoder();
        ByteArrayInputStream in = new ByteArrayInputStream(base64Decoder5.decodeBuffer(encodedImage));
        artwork.setPreserveRatio(true);
        Image image = new Image(in);
        artwork.setImage(image);
    }

    public void setText() throws ParseException {
        this.owner = SQLString("owner");
        artworkOwner.setText(owner);

        this.artworkTitle = SQLString("artwork_title");
        artworkTitleLabel.setText(artworkTitle.toUpperCase(Locale.ROOT));
        //System.out.println(artworkTitle);


        this.purchasePrice = intSQL("purchase_price");
        if(purchasePrice != 0) {
            purchasePriceLabel.setText("$"+purchasePrice);
            canPurchase = true;
        } else {
            purchasePriceLabel.setText("ARTWORK NOT AVAILABLE FOR PURCHASE.");
            canPurchase = false;
        }

        remainingTime();

        this.currentHighestBidder = SQLString("current_highest_bidder");
        if (currentHighestBidder == null) {
            currentHighestBidderLabel.setText("There is no current highest bidder.");
        } else {
            currentHighestBidderLabel.setText(currentHighestBidder + " is the highest bidder");
        }

        this.details = SQLString("details");
        detailsLabel.setText(details);

        this.tradingHistory = SQLString("bid_history");
        //add time to history print
        tradingHistoryLabel.setText(tradingHistory);

        int bidAmount = intSQL("bids_amount");
        bidsLabel.setText(String.valueOf(bidAmount) + " total bids");

    }

    public static int intSQL(String request) {
        int result = 0;
        String intCmd = "SELECT " + request + " FROM asteria.artwork WHERE piece_number = " + Params.PIECE_NUMBER_REQUEST;

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
        String stringCmd = "SELECT " + request + " FROM asteria.artwork WHERE piece_number = " + Params.PIECE_NUMBER_REQUEST;

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

    public void remainingTime() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);

        String result = "";
        String stringCmd = "SELECT end_time FROM asteria.artwork WHERE piece_number = " + Params.PIECE_NUMBER_REQUEST;

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

        Date endTime = dateFormat.parse(result);
        //System.out.println("Current Time: " + dateFormat.format(currentTime));
        //System.out.println("End time: " + dateFormat.format(endTime));

        seconds = (endTime.getTime() - currentTime.getTime()) / 1000;
        //System.out.println(seconds);

        if(seconds < 1) {
            timeLabel.setText("AUCTION EXPIRED");
            auctionTimeout = true;
            return;
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
                long totalSeconds = seconds;
                boolean run = true;
                while (true) {
                    Thread.sleep(1000);
                    //System.out.println(auctionTimeout);
                    totalSeconds--;
                    seconds--;
                    if(totalSeconds < 0) {
                        auctionTimeout();
                    }
                    long finalSeconds = totalSeconds;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            long p1 = finalSeconds % 60;
                            long p2 = finalSeconds / 60;
                            long p3 = p2 % 60;
                            p2 = p2 / 60;
                            timeLabel.setText(p2 + ":" + p3 + ":" + p1);
                            if(finalSeconds < 1) {
                                timeLabel.setText("AUCTION EXPIRED");
                            }
                        }
                    });
                }
            }
        };
        remainingTime = new Thread(task);
        remainingTime.setDaemon(true);
        remainingTime.start();
    }

    public void auctionTimeout() throws InterruptedException {
        auctionTimeout = true;
        //System.out.println(auctionTimeout);
        remainingTime.join();
    }

    public void refresh(MouseEvent mouseEvent) throws IOException, ParseException {

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
                    setImage();
                    remainingTime();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        refresh.start();
        rt.start();
        bidPrice();
        setText();
        refreshServer();
        invalidBidLabel.setText(" ");
    }

    private void refreshServer() {
        UploadMessage request = new UploadMessage("refresh");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        sendToServer(gson.toJson(request));
    }

    public void clickExit(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void minimizeWindow(MouseEvent mouseEvent) {
        minimizeWindow();
    }

    @FXML
    void purchaseClick(MouseEvent event) {
        if(!canPurchase) {
            invalidPurchaseLabel.setText("Not available for purchase.");
        }
        else if(Params.USER_FUNDS < purchasePrice) {
            invalidBidLabel.setText("Not enough funds to purchase this piece.");
        }
        else if(canPurchase && !auctionTimeout) {
            purchase();
            purchasePriceLabel.setText("Piece not available for purchase.");
            invalidPurchaseLabel.setText("You now own this piece.");
            canPurchase = false;
        }
    }

    private void purchase() {
        UploadMessage request = new UploadMessage("purchase", Params.USERNAME, purchasePrice, Params.PIECE_NUMBER_REQUEST, Params.USER_FUNDS, owner);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        sendToServer(gson.toJson(request));
    }



}
