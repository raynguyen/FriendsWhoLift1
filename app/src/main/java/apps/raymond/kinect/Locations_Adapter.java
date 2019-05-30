/*
 * Adapter class to populate a RecyclerView holding Location_Model items.
 *
 * We don't expect that the Location set will change without the user being directly involved in
 * modifying the list so we need not utilize a DiffUtil or observer for changes.
 */
package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Locations_Adapter extends RecyclerView.Adapter<Locations_Adapter.LocationViewHolder> {
    private List<Location_Model> mLocationSet = new ArrayList<>();
    private LocationClickInterface mClickInterface;

    public interface LocationClickInterface{
        void onLocationItemClick(Location_Model location);
    }

    public Locations_Adapter(LocationClickInterface clickInterface){
        mClickInterface = clickInterface;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_location,viewGroup,false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder vh, int i) {
        final Location_Model locationModel = mLocationSet.get(i);
        vh.txtLookup.setText(locationModel.getLookup());
        vh.txtAddress.setText(locationModel.getAddress());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickInterface.onLocationItemClick(locationModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setData(List<Location_Model> locationSet){
        mLocationSet.clear();
        mLocationSet.addAll(locationSet);
        notifyItemRangeChanged(0,mLocationSet.size());
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder{
        private TextView txtLookup, txtAddress;
        LocationViewHolder(View view){
            super(view);
            txtLookup = view.findViewById(R.id.text_lookup);
            txtAddress = view.findViewById(R.id.text_address);
        }

    }
}
