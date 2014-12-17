package habib.voip;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import habib.voip.adapters.ListAdapter;
import habib.voip.network.TCPConnect;
import habib.voip.network.TCPSender;
import habib.voip.sound.FrequencyBand;
import habib.voip.sound.SpeexDecoder;

public class MainActivity extends ActionBarActivity {
	public static final ArrayList<Integer> ARRAY_LIST = new ArrayList<Integer>();
	public static final int SAMPLE_RATE = 32000;
	private static MainActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        activity = this;
		setContentView(R.layout.activity_main);
		ListView userList = (ListView) findViewById(R.id.userList);
		userList.setAdapter(new ListAdapter(this,R.layout.array_list_item, ARRAY_LIST));
		new TCPConnect().execute();
	}

	public static MainActivity getActivity() {
		return activity;
	}

    @Override
    protected void onDestroy() {
        new TCPSender().executeContent(new byte[]{ Protocols.LOGOUT });
        super.onDestroy();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
