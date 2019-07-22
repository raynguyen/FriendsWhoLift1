
package apps.raymond.kinect;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

//ToDo: Consider implementing hashcode to individually identify event without comparing strings.
public class Event_Model implements Parcelable{
    public static final String SPORTS = "sports";
    public static final String DRINKS = "drinks";
    public static final String FOOD = "food";
    public static final String MOVIE = "movies";
    public static final String CHILL = "chill";
    public static final String CONCERT = "concert";
    public static final int EXCLUSIVE = 0; //Invitation only. Not visible on map.
    public static final int PRIVATE = 1; //Invitation only. Can request to attend.
    public static final int PUBLIC = 2; //Open to all users.
    private String creator, name, desc;
    private double lat, lng;
    private int privacy = PUBLIC;
    private List<String> tags, primes;
    private String address;
    private int attending; //Field starts at 0 by default as creation of the document in DB increments by one per user we add.
    private int invited;
    private long long1;
    private long create;

    public static final Parcelable.Creator<Event_Model> CREATOR = new Parcelable.Creator<Event_Model>(){
        @Override
        public Event_Model createFromParcel(Parcel source) {
            return new Event_Model(source);
        }

        @Override
        public Event_Model[] newArray(int size) {
            return new Event_Model[size];
        }
    };

    public Event_Model(){
    }

    public Event_Model(String creator, String name, String desc, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited){
        this.creator = creator;
        this.name = name;
        this.desc = desc;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        this.invited = invited;
    }

    public Event_Model(String creator, String name, String desc,int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, long long1){
        this.creator = creator;
        this.name = name;
        this.desc = desc;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        this.invited = invited;
        this.long1 = long1;
    }

    public Event_Model(String creator, String name, String desc, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, String address, double lat, double lng,
                       long long1) {
        this.creator = creator;
        this.name = name;
        this.desc = desc;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        this.invited = invited;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.long1 = long1;
    }

    @SuppressWarnings("unchecked")
    private Event_Model(Parcel in){
        creator = in.readString();
        name = in.readString();
        desc = in.readString();
        long1 = in.readLong();
        create = in.readLong();
        privacy = in.readInt();
        tags = in.readArrayList(null);
        primes = in.readArrayList(null);
        attending = in.readInt();
        invited = in.readInt();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public String getCreator(){
        return creator;
    }

    public void setCreator(String creator){
        this.creator = creator;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public int getPrivacy(){
        return privacy;
    }

    public void setPrivacy(int privacy){
        this.privacy = privacy;
    }

    public List<String> getTags(){
        return this.tags;
    }

    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }

    public List<String> getPrimes(){
        return primes;
    }

    public void setPrimes(List<String> primes){
        this.primes = primes;
    }

    public void setAttending(int i){
        attending = i;
    }
    public int getAttending(){
        return attending;
    }

    public void setInvited(int i){
        invited = i;
    }

    public int getInvited(){
        return invited;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public double getLat(){
        return lat;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

    public double getLng(){
        return lng;
    }

    public LatLng getLatLng(){
        return new LatLng(this.lat,this.lng);
    }

    public void setLong1(long long1){
        this.long1 = long1;
    }

    public long getLong1(){
        return long1;
    }

    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creator);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeLong(long1);
        dest.writeLong(create);
        dest.writeInt(privacy);
        dest.writeList(tags);
        dest.writeList(primes);
        dest.writeInt(attending);
        dest.writeInt(invited);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Event_Model)){
            return false;
        }
        return ((Event_Model) obj).getName().equals(this.name);
    }
}
