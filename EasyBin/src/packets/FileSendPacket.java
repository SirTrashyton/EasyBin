package packets;

/**
 * This packet is sent by the client when it wants to send a file to
 * another client. The server stores this packet and will send it back to the
 * requested client if they accept.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/22/19
 **/
public class FileSendPacket {
    public String PIN;
    public Byte[] content;
    public String fileName;
    public String path;
    public Integer fileSize;
    public Integer connection;
    public String UID;
}
