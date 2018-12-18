package apps.raymond.friendswholift;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LiftListAdapter extends ArrayAdapter<Lift> {

    private Context context;
    List<Lift> lifts;
    private static final SimpleDateFormat dateformat = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public LiftListAdapter(Context context, List<Lift> lifts){
        super(context, R.layout.lift_list, lifts);
        this.context = context;
        this.lifts = lifts;
    }

    private class ViewHolder{
        TextView liftIDText;
        TextView dateText;
        TextView weightText;
    }

    @Override
    public int getCount(){
        return lifts.size();
    }

    //The getItem returns the position of the object type 'Lift'.
    @Override
    public Lift getItem(int position){
        return lifts.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lift_list,null);
            holder = new ViewHolder();

            holder.liftIDText = (TextView) convertView.findViewById(R.id.txt_id);
            holder.dateText = (TextView) convertView.findViewById(R.id.txt_weight);
            holder.weightText = (TextView) convertView.findViewById(R.id.txt_weight);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Lift lift = (Lift) getItem(position);
        holder.liftIDText.setText(lift.getId()+"");
        holder.dateText.setText(dateformat.format(lift.getDate()));
        holder.weightText.setText(lift.getWeight()+"");

        return convertView;
    }

    @Override
    public void add(Lift lift){
        lifts.add(lift);
        notifyDataSetChanged();
        super.add(lift);
    }

    @Override
    public void remove(Lift lift){
        lifts.remove(lift);
        notifyDataSetChanged();
        super.remove(lift);
    }
}
