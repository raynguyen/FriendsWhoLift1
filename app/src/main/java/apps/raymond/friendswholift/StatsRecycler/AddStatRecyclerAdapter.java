package apps.raymond.friendswholift.StatsRecycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.raymond.friendswholift.R;

public class AddStatRecyclerAdapter extends RecyclerView.Adapter<AddStatRecyclerAdapter.ViewHolder>{

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView fieldText, inputText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldText = itemView.findViewById(R.id.field_txt);
            inputText = itemView.findViewById(R.id.input_txt);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
