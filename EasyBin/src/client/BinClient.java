package client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import mich.FileUtils;
import javafx.application.Platform;
import mich.Pref;
import packets.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/20/19
 **/
public class BinClient extends Listener {

    public ArrayList<ReceiveScreen> screens = new ArrayList<ReceiveScreen>();

    /**
     * Unique PIN given from the Server.
     */
    public String PIN;

    /**
     * Instance of our Kryo client.
     */
    private Client client;

    /**
     * Attempts to connect to the EasyBin server. A successful connection
     * would entail a received ConnectResponse packet.
     */
    public void init() {
        int m = 200;
        client = new Client( m * 8192, m * 2048);
        registerPackets(client);
        client.start();
        try {
            client.connect(Pref.CLIENT_TIMEOUT, Pref.SERVER_IP, Pref.PORT_TCP, Pref.PORT_UDP);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        client.addListener(this);
    }

    /**
     * Sends an object to the server in a separate thread.
     * @param object
     */
    public void send(Object object) {
        Runnable myRunnable =
                new Runnable(){
                    public void run(){
                        client.sendTCP(object);
                    }
                };
        new Thread(myRunnable).start();
    }

    /**
     * Gets the connection ID to the client.
     * @return
     */
    public int getID() {
        return client.getID();
    }

    /**
     * Invoked when we have been connected and the next screen
     * needs to be shown.
     * Called in the JavaFX thread.
     */
    public void connectionFinished() {
        LaunchScreen.instance.nextScreen();
    }

    /**
     * Invoked to register packets for the KryoServer.
     * @param client to register packets to.
     */
    public void registerPackets(Client client) {
        client.getKryo().register(ConnectResponsePacket.class);
        client.getKryo().register(FileAcceptPacket.class);
        client.getKryo().register(FileSendPacket.class);
        client.getKryo().register(FileSendRequestPacket.class);
        client.getKryo().register(PinCheckRequestPacket.class);
        client.getKryo().register(PinCheckResponsePacket.class);
        client.getKryo().register(PinUpdatePacket.class);
        client.getKryo().register(String.class);
        client.getKryo().register(Boolean.class);
        client.getKryo().register(Integer.class);
        client.getKryo().register(Byte[].class);
    }

    /**
     * Invoked when a valid connection has been established with the server.
     * @param connection
     */
    @Override
    public void connected(Connection connection) {
        super.connected(connection);
    }

    /**
     * Invoked when the client has disconnected from the server.
     * @param connection
     */
    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
    }

    /**
     * Invoked whenever this Client receives a Packet.
     * Make sure that all FX's activities are done within the FX thread.
     * @param connection
     * @param object received from the Server.
     */
    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);
        /*
        A response to our connect request have been received.
        Create our SendScreen with the given PIN.
         */
        if (object instanceof ConnectResponsePacket) {
            ConnectResponsePacket packet = (ConnectResponsePacket) object;
            if (packet.allowed) {
                PIN = packet.PIN;
                connectionFinished();
            }

        /*
        A response to our PIN check request has been sent.
        Update the booleans and buttons in the window (In a JavaFX thread).
         */
        } else if (object instanceof PinCheckResponsePacket) {
            PinCheckResponsePacket packet = (PinCheckResponsePacket) object;
            BinClient bc = this;
            //run in fx thread
            Runnable mr = () -> {
                SendScreen.getInstance().updateValid(packet.valid);
                SendScreen.getInstance().msc.updateButton();
            };
            Platform.runLater(mr);

        /*
        Another client wants to send us a a file.
        Create the ReceiveScreen with info from the given packet.
         */
        } else if (object instanceof FileSendRequestPacket) {
            BinClient bc = this;
            Runnable r = () -> ReceiveScreen.createInstance((FileSendRequestPacket) object, bc);
            Platform.runLater(r);
        /*
        The file has been accepted by both the client and the server, and now
        the given file must be created.
        ReceiveScreen should be removed as well.
         */
        } else if (object instanceof FileSendPacket) {
            //time to create our file given the FileSendPacket
            FileSendPacket fsp = (FileSendPacket) object;
            String path = fsp.path + "/" + fsp.fileName;
            FileUtils.writeFile(path, FileUtils.convert(fsp.content));
            ReceiveScreen.removeInstance(getScreen(fsp.PIN), this);
            /*
            A user disconnected! If our inputted PIN matches the one on the packet
            then we have to update the validity to false.
             */
        } else if (object instanceof PinUpdatePacket) {
            PinUpdatePacket packet = (PinUpdatePacket) object;
            if (SendScreen.getInstance().msc.textFieldPIN.getText().equals(packet.PIN)) SendScreen.getInstance().updateValid(false);
        } else {
            //unknown packet
        }
    }

    /**
     * Invoked when our client is idle with the server.
     * @param connection
     */
    @Override
    public void idle(Connection connection) {
        super.idle(connection);
    }

    /**
     *
     * @param pin
     * @return
     */
    public ReceiveScreen getScreen(String pin) {
        ReceiveScreen rs = null;
        for (ReceiveScreen screen: screens) {
            if (screen.rsc.packet.PIN.equals(pin)) rs = screen;
        }
        return rs;
    }
}
