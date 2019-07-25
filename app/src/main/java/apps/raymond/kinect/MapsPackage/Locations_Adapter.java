/*
 * Adapter class to populate a RecyclerView holding Location_Model items.
 *
 * We don't expect that the Location set will change without the user being directly involved in
 * modifying the list so we need not utilize a DiffUtil or observer for changes.
 */
package apps.raymond.kinect.MapsPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.R;

public class Locations_Adapter extends RecyclerView.Adapter<Locations_Adapter.LocationViewHolder> {
    private List<Location_Model> mLocationSet;
    private LocationClickInterface mClickInterface;

    public interface LocationClickInterface{
        void onLocationClick(Location_Model location);
    }

    public Locations_Adapter(LocationClickInterface clickInterface){
        mClickInterface = clickInterface;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_recycler_location,viewGroup,false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder vh, int i) {
        final Location_Model locationModel = mLocationSet.get(i);
        vh.txtLookup.setText(locationModel.getLookup());
        vh.txtAddress.setText(locationModel.getAddress());
        vh.itemView.setOnClickListener((View v)-> mClickInterface.onLocationClick(locationModel));
    }

    @Override
    public int getItemCount() {
        if(mLocationSet!=null){
            return mLocationSet.size();
        }
        return 0;
    }

    public void setData(List<Location_Model> locationSet){
        if(mLocationSet==null){
            mLocationSet = new ArrayList<>(locationSet);
            notifyItemRangeChanged(0,locationSet.size());
        } else {
            mLocationSet.clear();
            mLocationSet.addAll(locationSet);
            notifyItemRangeChanged(0,mLocationSet.size());
        }

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
