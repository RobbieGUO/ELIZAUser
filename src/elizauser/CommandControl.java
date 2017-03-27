package elizauser;

/**
 * This is used to send motor command(urbi) command to reeti through UDP
 *
 * @author Robbie
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandControl {

    //local port
    private int mLocalPort;

    //local address
    private InetAddress mRemotehost;
    private int mRemotePort;

    // the datagram socket
    private DatagramSocket mSocket;

    public CommandControl(int localPort, String rhost, int remotePort) {
        try {
            mLocalPort = localPort;
            mRemotehost = InetAddress.getByName(rhost);
            mRemotePort = remotePort;
        } catch (UnknownHostException ex) {
            Logger.getLogger(CommandControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void start() {
        // create UDP connection
        try {
            mSocket = new DatagramSocket(mLocalPort);
        } catch (SocketException ex) {
            Logger.getLogger(CommandControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Send some string via the socket
    public final boolean sendString(final String string) {
        try {
            // Create the byte buffer
            final byte[] buffer = string.getBytes("UTF-8");
            // Create the UDP packet
            final DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length, mRemotehost, mRemotePort);
            // And send the UDP packet
            mSocket.send(packet);
            // Print some information
            System.out.println("[CommandControl] Sending message:" + string);
            return true;
        } catch (final IOException exc) {
            System.out.println("[CommandControl] Sending failed");
            return false;
        }
    }

    // Receive some bytes via the socket
    public final byte[] recvBytes(final int size) {
        try {
            // Construct a byte array
            final byte[] buffer = new byte[size];
            // Construct an UDP packet
            final DatagramPacket packet
                    = new DatagramPacket(buffer, buffer.length);
            // Receive the UDP packet
            mSocket.receive(packet);
            // Return the buffer now
            return Arrays.copyOf(buffer, packet.getLength());
        } catch (final IOException exc) {
            System.out.println("[CommandControl] Receiving failed");
            return null;
        }
    }

    // Receive some string via the socket
    public final String recvString() {
        try {
            // Receive a byte buffer
            final byte[] buffer = recvBytes(4096);
            // Check the buffer content
            if (buffer != null) {
                // Construct a message
                final String message
                        = new String(buffer, 0, buffer.length, "UTF-8");
                // Print some information
                System.out.println("[CommandControl] Message received:" + message);
                // And return message
                return message;
            }
        } catch (final UnsupportedEncodingException exc) {
            // Print some information
            System.out.println("[CommandControl] Message not received");
        }
        return null;
    }
}
