package habib.voip;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import habib.voip.adapters.ListAdapter;
import habib.voip.network.TCPConnect;
import habib.voip.network.TCPSender;

public class MainActivity extends ActionBarActivity {
	public static final ArrayList<Integer> ARRAY_LIST = new ArrayList<>();
	public static final int SAMPLE_RATE = 32000;
	private static MainActivity activity;

    public static MainActivity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        ListView userList = (ListView) findViewById(R.id.userList);
        userList.setAdapter(new ListAdapter(this, R.layout.array_list_item, ARRAY_LIST));
        TCPConnect connect = new TCPConnect();
        connect.executeContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

    @Override
    protected void onResume() {
        Manager.getManager().setActivity(this);
        super.onResume();
    }


    @Override
    protected void onRestart() {
        Manager.getManager().setActivity(this);
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
        } else if (id == R.id.refresh_setting) {
            getUserList();
            return true;
        } else if (id == R.id.log_out) {
            logOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        new TCPSender().executeContent(new byte[]{Protocols.LOGOUT});
    }

    private void getUserList() {
        new TCPSender().executeContent(new byte[]{Protocols.GETLIST});
    }
}
