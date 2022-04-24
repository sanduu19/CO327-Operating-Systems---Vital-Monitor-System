/* E/17/251
 * This class implements a TCP connection.
 * Will be called by the Gateway class to create a new TCP connection with the Vital Monitors
 * Runs on a separate threads
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;

public class TCPConnection implements Runnable{
    private final InetAddress ip;
    private final String monitorID;
    private final int port;

    public TCPConnection (InetAddress ip, String monitorID, int port) {
        this.ip = ip;
        this.monitorID = monitorID;
        this.port = port;
    }

    public void createTCPConnection() {
        try {
            // create a client socket to connect to the monitor
            Socket clientSocket = new Socket(this.ip, this.port);

            // declaring input stream
            InputStream In_Stream = clientSocket.getInputStream();
            InputStreamReader In_Stream_Reader = new InputStreamReader(In_Stream);

            // string buffer to store the input stream
            StringBuffer buffer = new StringBuffer();

            // keep reading data from the monitor
            while (true) {
                // read data from the monitor
                int data = In_Stream_Reader.read();
                while (data != '\n') {
                    buffer.append((char) data);
                    data = In_Stream_Reader.read();
                }

                // break if the monitor sends "end"
                if (buffer.toString().equals("end")) {
                    break;
                }

                // print data received and thread id
                System.out.println(monitorID + ":\n" + buffer.toString() + " :: Thread ID: " + Thread.currentThread().getId());
                
                // clear buffer
                buffer.delete(0, buffer.length());
            }

            // close the socket
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // overrinding the run() method inherited from the Runnable interface
    @Override
    public void run() {
        this.createTCPConnection();
    }
}
