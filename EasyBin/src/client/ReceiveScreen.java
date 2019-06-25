package client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import packets.FileSendRequestPacket;

import java.io.IOException;

/**
 * This screen represents the screen used to recieve files. (ALERT SCREEN).
 * This screen can have infinite instances and are created from the FileSendRequestPacket, which
 * contains basic file information and sender info.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/23/19
 **/
public class ReceiveScreen {

    /**
     * Creates an instance of this class when a file is sent.
     */
    public static ReceiveScreen createInstance(FileSendRequestPacket packet, BinClient binClient) {
        ReceiveScreen rs = new ReceiveScreen();
        rs.init(packet, binClient);
        binClient.screens.add(rs);
        return rs;
    }

    /**
     * Removes the given instance
     * @param receiveScreen
     */
    public static void removeInstance(final ReceiveScreen receiveScreen, BinClient binClient) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                receiveScreen.primaryStage.hide();
            }
        };
        Platform.runLater(r);
        binClient.screens.remove(receiveScreen);
    }

    /**
     * Reference to our controller for this screen.
     */
    public ReceiveScreenController rsc;

    /**
     * Reference to our stage.
     */
    public Stage primaryStage;

    /**
     * Called to create the receive screen.
     * @param packet
     */
    public void init(FileSendRequestPacket packet, BinClient binClient) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/view_2.fxml"));
        //setup controller
        rsc = new ReceiveScreenController();
        primaryStage = new Stage();
        rsc.primaryStage = primaryStage;
        rsc.dc = new DirectoryChooser();
        rsc.binClient = binClient;
        rsc.packet = packet;
        loader.setController(rsc);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //setup stage
        primaryStage.setTitle("EasyBin - File Alert");
        primaryStage.setScene(new Scene(root, 400, 225));
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //final init step
        primaryStage.show();
    }
}
