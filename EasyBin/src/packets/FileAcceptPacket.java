package packets;

/**
 * Sent by the client when they have a File Alert and they want to accept
 * the incoming client's file.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/23/19
 **/
public class FileAcceptPacket {
    public String UID;
    public String path;
}
