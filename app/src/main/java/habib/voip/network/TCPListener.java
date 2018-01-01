package habib.voip.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import habib.voip.Manager;
import habib.voip.Protocols;
import habib.voip.base.AsyncBase;

/**
 * Created by Habib on 1.12.2014.
 */
public class TCPListener extends AsyncBase<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        Manager manager = Manager.getManager();
        Protocols protocols = new Protocols();
        while (!manager.getSocket().isClosed()) {
            byte[] buffer = new byte[1000];
            try {
                manager.getSocket().getInputStream().read(buffer);
                ByteBuffer wrap = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
                protocols.HandlePackage(wrap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
