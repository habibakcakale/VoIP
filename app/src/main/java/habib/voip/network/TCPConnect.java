package habib.voip.network;

import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

import habib.voip.Manager;
import habib.voip.Protocols;
import habib.voip.Values;

public class TCPConnect extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			Manager manager = Manager.getManager();
			manager.setSocket(new Socket(Values.IP, Values.PORT));
			manager.getSocket().getOutputStream().write(Protocols.GETLIST);
            new TCPListener().executeContent();
            Log.i(Values.LogTag,"Connection successes");
        } catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
