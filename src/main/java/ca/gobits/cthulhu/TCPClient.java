package ca.gobits.cthulhu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * TCPClient.
 */
public final class TCPClient {

    /**
     * private constructor.
     */
    private TCPClient() {
    }

    /**
     * @param argv argv
     * @throws Exception Exception
     */
    public static void main(final String[] argv) throws Exception {
        Socket clientSocket = new Socket("localhost", DHTServer.DEFAULT_PORT);
        DataOutputStream outToServer = new DataOutputStream(
                clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream()));
        String sentence =
                "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";
        outToServer.writeBytes(sentence + '\n');
        String modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }
}
