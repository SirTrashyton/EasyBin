package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import mich.Pref;
import packets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class contains an executable Server which will
 * listen for the following packets:
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/20/19
 **/
public class BinServer extends Listener {

    /**
     * Singelton of this class.
     */
    private static BinServer instance;

    /**
     * Instance of our Kryo server obj.
     */
    private Server server;

    /**
     * Map with the connection ID and their PIN.
     */
    public HashMap<Integer, String> pinMap = new HashMap<>();

    /**
     * Que of our FileSendObj (servers who want to send a file)
     */
    public ArrayList<FileSendPacket> sendQue = new ArrayList<>();

    /**
     * SendScreen program to set the instance and invoke 'init'.
     * @param args given arguments which we will ignore.
     */
    public static void main(String[] args) {
        instance = new BinServer();
        instance.init();
    }

    /**
     * Invoked to setup the Server.
     * Need to listen to 'ConnectResponsePacket' and send a response.
     */
    public void init() {
        int m = 200;
        server = new Server(m * 16384, m * 2048);
        registerPackets(server);
        try {
            server.bind(Pref.PORT_TCP, Pref.PORT_UDP);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        server.addListener(this);
        server.start();
    }

    /**
     * Helper function to send the given Object giver TCP.
     * @param object to send over the network.
     */
    public void send(int connection, Object object) {
        server.sendToTCP(connection, object);
    }

    /**
     * Invoked to register packets for the KryoServer.
     * @param server to register packets to.
     */
    public void registerPackets(Server server) {
        server.getKryo().register(ConnectResponsePacket.class);
        server.getKryo().register(FileAcceptPacket.class);
        server.getKryo().register(FileSendPacket.class);
        server.getKryo().register(FileSendRequestPacket.class);
        server.getKryo().register(PinCheckRequestPacket.class);
        server.getKryo().register(PinCheckResponsePacket.class);
        server.getKryo().register(PinUpdatePacket.class);
        server.getKryo().register(String.class);
        server.getKryo().register(Boolean.class);
        server.getKryo().register(Integer.class);
        server.getKryo().register(Byte[].class);
    }


    /**
     * Invoked when a server is connected.
     * A connection response packet shall be created and sent to the server.
     * @param connection obj assigned to the server.
     */
    @Override
    public void connected(Connection connection) {
        super.connected(connection);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConnectResponsePacket crp = new ConnectResponsePacket();
        crp.allowed = true;
        crp.PIN = PINGenerator.gen();
        pinMap.put(connection.getID(), crp.PIN);
        server.sendToTCP(connection.getID(), crp);
    }

    /**
     * Invoked when a client disconnects from this server.
     * Update the PinMap and the list of generated PINs.
     * Sends the PinUpdatePacket to all clients.
     * @param connection
     */
    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
        if (pinMap.containsKey(connection.getID())) {
            PINGenerator.removePIN(pinMap.get(connection.getID()));
            PinUpdatePacket packet = new PinUpdatePacket();
            packet.PIN = pinMap.get(connection.getID());
            server.sendToAllTCP(packet);
            pinMap.remove(connection.getID());
        }
    }

    /**
     * Invoked when this server received any packets.
     * @param connection
     * @param object
     */
    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);
        /*
        A client sent us a request to check a given PIN.
        Check if its valid by checking the list of generated pins
        Ignore own pin
         */
        if (object instanceof PinCheckRequestPacket) {
            PinCheckRequestPacket packet = (PinCheckRequestPacket) object;
            PinCheckResponsePacket pcrp = new PinCheckResponsePacket();
            pcrp.valid = PINGenerator.isValidPin(packet.PIN);
            //not valid if client submitted own PIN
            if (pinMap.get(connection.getID()).equals(packet.PIN)) pcrp.valid = false;
            send(connection.getID(), pcrp);

        /*
        The server received a request from a client to send a File.
        Store the packet and send the requested client an alert window (FileSendRequestPacket).
         */
        } else if (object instanceof FileSendPacket) {
            //register the file send
            FileSendPacket packet = (FileSendPacket) object;
            //gen UID
            packet.UID = PINGenerator.gen(8);
            sendQue.add(packet);
            //send out a request to da requested user
            FileSendRequestPacket rsrp = new FileSendRequestPacket();
            rsrp.fileName = packet.fileName;
            rsrp.fileSize = packet.fileSize;
            rsrp.PIN = packet.PIN;
            rsrp.UID = packet.UID;
            //find user's connection based on PIN
            int con = 0;
            for (Integer i: pinMap.keySet()) {
                String pin  = pinMap.get(i);
                if (pin.equals(rsrp.PIN)) con = i;
            }
            send(con, rsrp);
        /*
        A client accepts a given packet and needs to be sent the FileSendPacket.
         */
        } else if (object instanceof FileAcceptPacket){
            //get the FileSendPacket associated with the FileAcceptPacket's PIN
            FileAcceptPacket packet = (FileAcceptPacket) object;
            String UID = packet.UID;
            FileSendPacket fsp = getFileSendPackets(UID);
            fsp.path = packet.path;
            send(connection.getID(), fsp);
        } else if (object instanceof FileRejectedPacket) {
            FileRejectedPacket packet = (FileRejectedPacket) object;
            //find our FileSendPacket from the que and remove it bruh
            Iterator<FileSendPacket> it = sendQue.iterator();
            while (it.hasNext()) {
                FileSendPacket p = it.next();
                if (p.UID.equals(packet.UID)) {
                    it.remove();
                    break;
                }
            }
        } else {
            //unknown packet
        }
    }

    /**
     * Gets a FileSendPacket that matches the inputted connection.
     * @param UID of the packet that we want.
     */
    public FileSendPacket getFileSendPackets(String UID) {
        FileSendPacket fsp = null;
        for (FileSendPacket packets: sendQue) {
            if (packets.UID.equals(UID)) fsp = packets;
        }
        return fsp;
    }

    /**
     * Invoked when a client becomes idle.
     * @param connection
     */
    @Override
    public void idle(Connection connection) {
        super.idle(connection);
    }
}
