package client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the screen in which you can send files to
 * other user's by entering their PIN's.
 *
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/20/19
 **/
public class SendScreen {

    /**
     * Ref to our controller for this screen.
     */
    public SendScreenController msc;

    private static SendScreen instance;

    public static SendScreen getInstance() {
        if (instance == null) return instance = new SendScreen();
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    /**
     * Invoked to setup the Main screen.
     */
    public void init(String PIN, BinClient binClient) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/view_1.fxml"));
        //setup controller
        msc = new SendScreenController();
        Stage primaryStage = new Stage();
        msc.binClient = binClient;
        msc.primaryStage = primaryStage;
        msc.PIN = PIN;
        msc.fc = new FileChooser();
        loader.setController(msc);
        //load
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //setup stage
        primaryStage.setTitle("EasyBin - Client");
        primaryStage.setScene(new Scene(root, 600, 337));
        primaryStage.setResizable(false);
        //final init step
        primaryStage.show();
    }

    /**
     *
     * @param valid
     */
    public void updateValid(boolean valid) {
        Runnable r = () -> {
            msc.valid = valid;
            if (valid) {
                msc.labelValid.setText("Valid");
            } else {
                msc.labelValid.setText("Not Valid");
                msc.textFieldPIN.setText("");
            }
        };
        Platform.runLater(r);

    }
}