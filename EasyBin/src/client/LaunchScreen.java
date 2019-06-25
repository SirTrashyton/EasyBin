package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/20/19
 **/
public class LaunchScreen extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    private BinClient binClient;

    public static LaunchScreen instance;

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/view_0.fxml"));
        //setup controller
        LaunchScreenController lc = new LaunchScreenController();
        binClient = new BinClient();
        lc.binClient = binClient;
        loader.setController(lc);
        //load
        Parent root = loader.load();
        //setup stage
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("EasyBin - Connecting...");
        primaryStage.setScene(new Scene(root, 400, 225));
        primaryStage.setResizable(false);
        //final init step
        primaryStage.show();
        Runnable myRunnable =
                new Runnable(){
                    public void run(){
                        binClient.init();
                    }
                };
        new Thread(myRunnable).start();
        this.primaryStage = primaryStage;

        instance = this;
    }

    /**
     * Reference to our stage.
     */
    private Stage primaryStage;

    /**
     * Invoked to take us to the next screen
     */
    public void nextScreen() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                primaryStage.hide();
                SendScreen.getInstance().init(binClient.PIN, binClient);
            }
        };
        Platform.runLater(r);
    }
}

/**
 * This class represents a controller for "view_0" which
 * is the initial connection screen.
 */
class LaunchScreenController {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button buttonRetry, buttonExit;

    /**
     * Instance of our BinClient assigned by the parent loader.
     */
    public BinClient binClient;

    /**
     * Function that's called when the view is created.
     */
    @FXML
    private void initialize() {
        buttonRetry.setFocusTraversable(false);
        buttonExit.setFocusTraversable(false);
        //setup exit button
        buttonExit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
        //setup retry button
        buttonRetry.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                binClient.init();
            }
        });
    }
}
