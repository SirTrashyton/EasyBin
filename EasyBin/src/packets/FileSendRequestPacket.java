package packets;

/**
 * This class represents a basic request from the Server to the Client
 * asking for a file to be sent to another client.
 *
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/22/19
 **/
public class FileSendRequestPacket {
    public String PIN;
    public String fileName;
    public Integer fileSize;
    public String UID;
}
