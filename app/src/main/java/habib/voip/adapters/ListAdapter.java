package habib.voip.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import habib.voip.Protocols;
import habib.voip.R;
import habib.voip.Values;
import habib.voip.network.TCPSender;

/**
 * Created by Habib on 1.12.2014.
 */
public class ListAdapter extends ArrayAdapter<Integer> {
    Context context;
    int layoutResourceId;
    List<Integer> list;

    public ListAdapter(Context context, int resource, List<Integer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if (row == null) {
            LayoutInflater inflatter = ((Activity) context).getLayoutInflater();
            row = inflatter.inflate(layoutResourceId, parent, false);
            holder = new Holder();
            holder.text = (TextView) row.findViewById(R.id.rowtext);
            holder.callButton = (Button) row.findViewById(R.id.callbutton);
            holder.callButton.setOnClickListener(holder);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }
        int item = list.get(position);
        holder.text.setText(String.valueOf(item));
        holder.id = item;
        return row;
    }

    static class Holder implements View.OnClickListener {
        int id;
        TextView text;
        Button callButton;

        @Override
        public void onClick(View view) {
            byte[] call = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN).put(Protocols.CALL).putInt(id).array();
            Log.i(Values.LogTag, "Arama isteği " + String.valueOf(id) + " için yapıldı");
            new TCPSender().executeContent(call);
        }
    }
}
