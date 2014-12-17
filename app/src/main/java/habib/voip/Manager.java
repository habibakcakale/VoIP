package habib.voip;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class Manager {
	private static Manager manager;
	private Socket socket;
    private DatagramSocket udpSocket;
    public InetAddress ConnectedIpAddress;
    public int ConnectedPort = -1;
	private Manager() {
        try {
            udpSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

	public static Manager getManager() {
		return manager == null ? manager = new Manager() : manager;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }
}
