package apps.raymond.kinect;

import android.os.Parcel;
import android.os.Parcelable;

public class Location_Model implements Parcelable {
    private String lookup;
    private String address;
    private double lat;
    private double lng;

    public static final Parcelable.Creator<Location_Model> CREATOR
            = new Parcelable.Creator<Location_Model>(){
        @Override
        public Location_Model createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public Location_Model[] newArray(int size) {
            return new Location_Model[0];
        }
    };

    public Location_Model(String lookup,String address, Double lat, Double lng){
        this.lookup = lookup;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public Location_Model(Parcel in){
        lookup = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lookup);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public String getLookup() {
        return lookup;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
