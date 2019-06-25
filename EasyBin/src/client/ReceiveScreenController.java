package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import packets.FileAcceptPacket;
import packets.FileRejectedPacket;
import packets.FileSendRequestPacket;
import packets.PinCheckRequestPacket;
import server.PINGenerator;

import java.io.File;

/**
 * This class represents a basic view controller for 'ReceieveScreen.java'.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/23/19
 **/
public class ReceiveScreenController {



    /**
     * Reference to our send req packet.
     */
    public FileSendRequestPacket packet;

    /**
     * Reference to our BinClient.
     */
    public BinClient binClient;

    @FXML
    private Button buttonExit;

    @FXML
    private TextField textFieldDir;

    /**
     * Reference to our labels
     */
    @FXML
    public Label labelPIN, labelFileName, labelFileSize;

    @FXML
    private Button buttonReceive;

    /**
     * Reference to our stage.
     */
    public Stage primaryStage;

    /**
     * Reference to our Dir Chooser
     */
    public DirectoryChooser dc;

    /**
     * Diectory chosen file, can be nul
     */
    private File file;

    /**
     * Function that's called when the view is created.
     */
    @FXML
    private void initialize() {
        //instance of dis class for our inner classes
        //to use
        ReceiveScreenController dis = this;
        //setup exit button
        buttonExit.setOnMouseClicked(event -> {
            FileRejectedPacket p = new FileRejectedPacket();
            p.UID = packet.UID;
            binClient.send(p);
            primaryStage.close();
        });
        labelPIN.setText(packet.PIN);
        labelFileName.setText(packet.fileName);
        labelFileSize.setText(packet.fileSize + " bytes");

        textFieldDir.setText("CHOOSE DIR");
        textFieldDir.setOnMouseClicked(event -> {
            File file = dc.showDialog(primaryStage.getScene().getWindow());
            if (file != null) {
                dis.file = file;
                textFieldDir.setText(dis.file.getAbsolutePath());
                buttonReceive.setDisable(false);
            } else {
                dis.file = null;
                textFieldDir.setText("CHOOSE DIR");
                buttonReceive.setDisable(true);
            }
        });
        textFieldDir.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue) {
                //focused
                textFieldDir.setText("");
            } else {
                //unfocused
            }
        });

        //setup recieve button
        buttonReceive.setOnMouseClicked(event -> {
            //lets send a FileAcceptPacket
            FileAcceptPacket packet = new FileAcceptPacket();
            packet.UID = dis.packet.UID;
            packet.path = file.getAbsolutePath();
            binClient.send(packet);
        });
    }
}
