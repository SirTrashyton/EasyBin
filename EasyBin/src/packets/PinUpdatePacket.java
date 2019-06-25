package packets;

/**
 * This packet is sent from the Server to all clients when a user with
 * the given PIN disconnects.
 * The client needs to update its validity if its PIN matches this packet's PIN.
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/25/19
 **/
public class PinUpdatePacket {
    public String PIN;
}
