package apps.raymond.friendswholift;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomLiftAdapter extends SimpleCursorAdapter {
    private static final String yes = "Temporary.";
    private int layout;
    private Context context;

    public CustomLiftAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                             int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(layout,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    TextView dateText = (TextView) view.findViewById(R.id.date_text);
    TextView typeText = (TextView) view.findViewById(R.id.type_text);
    TextView weightText = (TextView) view.findViewById(R.id.weight_text);

    //dateText.setText(cursor.getString(cursor.getColumnIndex("date")));
    dateText.setText(yes);
    typeText.setText(cursor.getString(cursor.getColumnIndex("type")));
    weightText.setText(cursor.getString(cursor.getColumnIndex("weight")));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        return convertView;
    }
}
