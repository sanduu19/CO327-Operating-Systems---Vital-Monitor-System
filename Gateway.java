// E/17/251
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.SocketException;

public class Gateway {
    // ArrayList which stores the list of monitors connected with the TCP connection
    static private List<String> connectedMoniters = new ArrayList<String>();

    public static void main(String[] args) {
        // UDP broadcast port
        int UDP_PORT = 6000;

        // create a datagram socket to receive broadcast messages
        DatagramSocket rcv_Socket = createBroadcastSocket(UDP_PORT);
        System.out.println("Gateway Running ...\nListening to port 6000 ...");

        // keep receiving broadcast messages and create TCP connections with monitors
        while (true) {
            // Following methods waits for a new UDP packet
            DatagramPacket rcv_Packet = rcv_Packet(rcv_Socket);

            // reading the monitor object from the received byte stream
            Monitor monitor = getMonitorObj(rcv_Packet);

            // get the ip address and port of the monitor
            InetAddress ipAddress = monitor.getIp();
            int port = monitor.getPort();
            String monitor_ID = monitor.getMonitorID();

            // check whether the monitor is already in the Monitor list
            if (!connectedMoniters.contains(ipAddress + ":" + port)) {
                // add the monitor to the list as a string
                connectedMoniters.add(ipAddress + ":" + port);

                // print monitor information
                System.out.println("Connection Established! \nmonitor info: " + ipAddress + ":" + port + " /" + monitor_ID);

                // create tcp thread to connect to the monitor
                Thread tcpConnection = new Thread(new TCPConnection(ipAddress, monitor_ID, port));

                // start the tcp connection thread
                tcpConnection.start();
            }
        }
    }

    // this methods gets the monitor object from the data stream received
    private static Monitor getMonitorObj(DatagramPacket rcv_Packet) {
        Monitor monitor = null;
        try {
            // get the monitor object from the received packet
            InputStream in = new ByteArrayInputStream(rcv_Packet.getData()); // storing the packet in the input stream buffer
            ObjectInputStream ois = new ObjectInputStream(in); 
            monitor = (Monitor) ois.readObject(); // reading the object (typecasting for a Monitor object) 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monitor;
    }

    // this methods receives a single datagram packet on the socket given as the argument
    private static DatagramPacket rcv_Packet(DatagramSocket rcv_Socket) {
        DatagramPacket rcv_Packet = null;
        try {
            // creating a byte array as athe buffer for receiving Datagram packet
            byte[] rcv_buffer = new byte[1024];

            // cconstructs a datagram packet to receive data from the UDP socket
            rcv_Packet = new DatagramPacket(rcv_buffer, rcv_buffer.length);

            // this method will wait until it gets a new UDP packet
            rcv_Socket.receive(rcv_Packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // returns the received UDP packet
        return rcv_Packet;
    }

    // this method creates the Datagram socket which will receive all the broadcast messages to the specified port
    private static DatagramSocket createBroadcastSocket(int UDP_PORT) {
        DatagramSocket Broadcast_Socket = null;
        try {
            Broadcast_Socket = new DatagramSocket(UDP_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return Broadcast_Socket;
    }
}
