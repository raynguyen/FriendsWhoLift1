package apps.raymond.friendswholift.Activity_Main;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import apps.raymond.friendswholift.R;


public class StatTypeCursorAdapter extends SimpleCursorAdapter {

    private Context context;

    public StatTypeCursorAdapter(Context context, int layout, Cursor c, String[] from,
                             int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.lift_details, parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dateText = view.findViewById(R.id.id_text);
        TextView typeText = view.findViewById(R.id.type_text);
        TextView weightText = view.findViewById(R.id.weight_text);

        //dateText.setText(cursor.getString(cursor.getColumnIndex("date")));
        dateText.setText(String.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))));
        typeText.setText(cursor.getString(cursor.getColumnIndex("type")));
        weightText.setText(cursor.getString(cursor.getColumnIndex("weight")));
    }
}
