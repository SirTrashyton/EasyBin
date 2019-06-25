package packets;

/**
 * This packet is a simple connection response that is sent by
 * the Server to the client.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/20/19
 **/
public class ConnectResponsePacket {
    public Boolean allowed;
    public String PIN;
}
