package habib.voip.network;

import android.util.Log;

import java.net.Socket;
import java.nio.ByteBuffer;

import habib.voip.Manager;
import habib.voip.Protocols;
import habib.voip.Values;
import habib.voip.base.AsyncBase;

public class TCPConnect extends AsyncBase<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            Manager manager = Manager.getManager();
            manager.setSocket(new Socket(Values.IP, Values.PORT));
            //Send Public Key Information to server
            new TCPListener().executeContent();
            Log.i(Values.LogTag, "Connection successes");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i(Values.LogTag, "GET List For First Time");
        new TCPSender().executeContent(new byte[]{Protocols.GETLIST});

        Manager manager = Manager.getManager();
        byte[] modulus = manager.getSecurityManager().getModulusBytes();
        byte[] exponent = manager.getSecurityManager().getExponentBytes();
        ByteBuffer buffer = Protocols.allocateBuffer(Byte.SIZE + Integer.SIZE + (modulus.length * Byte.SIZE) + Integer.SIZE + (exponent.length * Byte.SIZE));
        buffer.put(Protocols.CONNECT).putInt(modulus.length).put(modulus).putInt(exponent.length).put(exponent);
        new TCPSender().executeContent(buffer.array());
    }
}
