package habib.voip.network;

import android.util.Log;

import java.io.IOException;

import habib.voip.Manager;
import habib.voip.Values;
import habib.voip.base.AsyncBase;

/**
 * Created by Habib on 1.12.2014.
 */
public class TCPSender extends AsyncBase<byte[], Void, Void> {
    @Override
    protected Void doInBackground(byte[]... bytes) {

        try {
            if (bytes != null && bytes[0] != null)
                Log.i(Values.LogTag, "" + (Manager.getManager() == null));
            Log.i(Values.LogTag, "" + (Manager.getManager().getSocket() == null));
            Log.i(Values.LogTag, "" + (Manager.getManager().getSocket().getOutputStream() == null));
            Manager.getManager().getSocket().getOutputStream().write(bytes[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
