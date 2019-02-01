/*
The RecyclerView needs an adapter to populate the views of each item.
 */
package apps.raymond.friendswholift.StatsRecycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.StatSQLClasses.StatEntity;

public class StatRecyclerAdapter extends RecyclerView.Adapter<StatRecyclerAdapter.StatViewHolder> {
    private static final String TAG = "StatRecyclerAdapter";
    private Context mContext;

    public StatRecyclerAdapter(Context context){
        this.mContext = context;
    }

    static class StatViewHolder extends RecyclerView.ViewHolder{
        private TextView typeText, valueText, dateText;

        private StatViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = (TextView) itemView.findViewById(R.id.item_type);
            valueText = (TextView) itemView.findViewById(R.id.item_value);
            dateText = (TextView) itemView.findViewById(R.id.item_date);
        }
    }

    private List<StatEntity> mStats; // Cached copy of StatEntity items.

    /*
     * This method is called every time a new ViewHolder is needed (i.e. there is data we want to
     * present on in the RecyclerView still.
     */
    @NonNull
    @Override
    public StatRecyclerAdapter.StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_stat_item,
                parent,false);
        return new StatViewHolder(view);
    }

    /*
     * This method is called following every onCreateViewHolder. It simply binds the information for
     * the recently created ViewHolder.
     * Position is an integer counter of the ViewHolder in it's parent view group.
     */
    @Override
    public void onBindViewHolder(@NonNull StatRecyclerAdapter.StatViewHolder holder, int position) {
        //get element from your dataset at this position
        //replace the contents of the view with that element
        if (mStats != null) {
            StatEntity current = mStats.get(position); // List.get(i) returns the element at i.
            holder.typeText.setText(current.getType());
            holder.valueText.setText(String.valueOf(current.getValue()));
            holder.dateText.setText(R.string.cancel);
        } else {
            holder.typeText.setText(R.string.app_name);
        }
    }

    public void setStats(List<StatEntity> stats){
        mStats = stats;
        notifyDataSetChanged(); // Notifies any attached observers that the data has changed.
    }

    @Override
    public int getItemCount() {
        if (mStats != null){
            return mStats.size();
        } else {
            return 0;
        }
    }
}
