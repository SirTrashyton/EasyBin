package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import packets.FileSendPacket;
import packets.PinCheckRequestPacket;
import server.PINGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/22/19
 **/
public class SendScreenController {

    /**
     * Reference to our BinClient so we can send packets.
     */
    public BinClient binClient;

    /**
     * Reference to our stage set during the initiation of this controller.
     */
    public Stage primaryStage;

    /**
     * PIN set during initiation of this controller.
     */
    public String PIN;

    /**
     * Reference to our FileChooser obj
     */
    public FileChooser fc;

    /**
     * File selected in the text field 'select'.
     */
    public File file;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public Label labelPIN, labelValid;

    @FXML
    public TextField textFieldPIN, textFieldSelect;

    @FXML
    private Button buttonSend;

    /**
     * Boolean to determine if a valid PIN has been placed.
     */
    public boolean valid = false;

    /**
     * Function that's called when the view is created.
     */
    @FXML
    private void initialize() {
        labelPIN.setText(PIN);

        textFieldPIN.setFocusTraversable(false);
        textFieldSelect.setFocusTraversable(false);
        textFieldSelect.setEditable(false);

        //make anchorPane clickable
        anchorPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.requestFocus();
            }
        });

        //open the file selector for select field
        textFieldSelect.setText("...");
        textFieldSelect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File f = fc.showOpenDialog(primaryStage);
                if (f != null) {
                    textFieldSelect.setText(f.getAbsolutePath());
                    file = f;
                } else {
                    //null file selected
                    textFieldSelect.setText("...");
                    file = null;
                    buttonSend.setDisable(true);
                }
                updateButton();
            }
        });
        //this text field should ask the server if its pin is valid
        textFieldPIN.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                String str = textFieldPIN.getText();
                if (newPropertyValue) {
                    //focused
                } else {
                    //unfocused
                    if (str.length() != PINGenerator.LENGTH) {
                        //not a valid str
                        textFieldPIN.setText("");
                        buttonSend.setDisable(true);
                        valid = false;
                    } else {
                        //submit a packet for a validity check
                        PinCheckRequestPacket packet = new PinCheckRequestPacket();
                        packet.PIN = str;
                        binClient.send(packet);
                    }
                }
                updateButton();
            }
        });
        //take away focus upon enter event
        textFieldPIN.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    labelValid.requestFocus();
                }
                updateButton();
            }
        });
        buttonSend.setDisable(true);
        buttonSend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendFile();
            }
        });
    }

    /**
     * Invoked to update our send button.
     */
    public void updateButton() {
        boolean rdy = file != null && valid;
        if (!rdy) {
            buttonSend.setDisable(true);
        } else {
            buttonSend.setDisable(false);
        }
    }

    /**
     * Helper function to send a file to another client.
     */
    public void sendFile() {
    //    String contents = getFileAsBinary(file);
      //  System.out.println(contents);
        FileSendPacket fsp = new FileSendPacket();
        fsp.connection = binClient.getID();
        fsp.content = getFileAsBinary(file);
        fsp.fileName = file.getName();
        fsp.fileSize = (int) file.length();
        fsp.PIN = textFieldPIN.getText();
        binClient.send(fsp);
    }

    public static Byte[] getFileAsBinary(File file) {
        Byte[] data;
        byte[] b = null;
        try {
           b = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = new Byte[b.length];
        int i = 0;
        for (byte bb: b) {
            data[i++] = (Byte) bb;
        }
        return data;
    }

}
