package packets;

/**
 * Sent from the client if they denied a File send request.
 * The server needs to remove the packet from the QUE.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/25/19
 **/
public class FileRejectedPacket {
    public String UID;
}
