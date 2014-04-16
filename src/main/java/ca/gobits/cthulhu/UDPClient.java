package ca.gobits.cthulhu;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDPClient.
 */
public final class UDPClient {

    /** MAX LENGTH. */
    private static final int MAX_LENGTH = 1024;

    /** PORT. */
    private static final int PORT = 6881;

    /**
     * private constructor.
     */
    private UDPClient() {
    }

    /**
     * @param args  args
     * @throws Exception  Exception
     */
    public static void main(final String[] args) throws Exception {
        // BufferedReader inFromUser =
        // new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress ipAddress = InetAddress.getByName("router.bittorrent.com");
        byte[] sendData = new byte[MAX_LENGTH];
        byte[] receiveData = new byte[MAX_LENGTH];
        String sentence =
                "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, ipAddress, PORT);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        byte[] bytes = modifiedSentence.getBytes();
        System.out.println(bytes.length);
        for (byte b : bytes) {
            System.out.println(b);
        }
        clientSocket.close();
    }
}
