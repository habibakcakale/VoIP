package habib.voip;

import android.app.Activity;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import habib.voip.crypto.SecurityManager;

public class Manager {
    private static Manager manager = new Manager();
    public InetAddress ConnectedIpAddress;
    public int ConnectedPort = -1;
    private Socket socket;
    private DatagramSocket udpSocket;
    private Activity activity;
    private SecurityManager securityManager;

    private Manager() {
        try {

            securityManager = new SecurityManager();
            securityManager.generateKeys(Values.KEYSIZE);
            udpSocket = new DatagramSocket();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static Manager getManager() {
        return manager;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    //public void setUdpSocket(DatagramSocket socket){
    //   this.udpSocket = socket;
    //}
    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}
