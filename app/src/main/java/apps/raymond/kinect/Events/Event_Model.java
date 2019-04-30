
package apps.raymond.kinect.Events;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event_Model implements Parcelable{
    public static final int PUBLIC = 0; //Anyone may join the event without prior connections to guests.
    public static final int CLOSED = 1; //Only users with connected guests may join the event.
    public static final int EXCLUSIVE = 2; //Invitation only
    public static final String SPORTS = "Sports";
    public static final String FOOD = "Food";
    public static final String DRINKS = "Drinks";
    public static final String MOVIES = "Movies";
    public static final String CHILL = "Chill";

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

    private String creator, originalName, name, desc, month1, month2, day1, day2;
    private double lat, lng;
    private int privacy;
    private List<String> tags, primes;
    private String address = "TBD";
    private int attending = 1;
    private int invited;
    private long long1, long2;
    @ServerTimestamp
    private Date date1, date2;

    public Event_Model(){
        // Empty constructor as required by FireBase.
    }

    public Event_Model(String creator, String name, String desc, String month1, String day1,
                       String month2, String day2, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited){
        this.creator = creator;
        this.name = name;
        this.originalName = name;
        this.desc = desc;
        this.month1 = month1;
        this.day1 = day1;
        this.month2 = month2;
        this.day2 = day2;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        this.invited = invited;
    }

    public Event_Model(String creator, String name, String desc, String month1, String day1,
                       String month2, String day2, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, Date date1, Date date2){
        this.creator = creator;
        this.name = name;
        this.originalName = name;
        this.desc = desc;
        this.month1 = month1;
        this.day1 = day1;
        this.month2 = month2;
        this.day2 = day2;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        this.invited = invited;
        this.date1 = date1;
        this.date2 = date2;
    }

    public Event_Model(String creator, String name, String desc, String month1, String day1,
                       String month2, String day2, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, String address, double lat, double lng){
        this.creator = creator;
        this.name = name;
        this.originalName = name;
        this.desc = desc;
        this.month1 = month1;
        this.day1 = day1;
        this.month2 = month2;
        this.day2 = day2;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        attending = 1;
        this.invited = invited;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public Event_Model(String creator, String name, String desc, String month1, String day1,
                       String month2, String day2, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, String address, double lat, double lng,
                       Date date1, Date date2){
        this.creator = creator;
        this.name = name;
        this.originalName = name;
        this.desc = desc;
        this.month1 = month1;
        this.day1 = day1;
        this.month2 = month2;
        this.day2 = day2;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        attending = 1;
        this.invited = invited;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.date1 = date1;
        this.date2 = date2;
    }

    public Event_Model(String creator, String name, String desc, String month1, String day1,
                       String month2, String day2, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, String address, double lat, double lng,
                       Date date1){
        this.creator = creator;
        this.name = name;
        this.originalName = name;
        this.desc = desc;
        this.month1 = month1;
        this.day1 = day1;
        this.month2 = month2;
        this.day2 = day2;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        attending = 1;
        this.invited = invited;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.date1 = date1;
    }

    @SuppressWarnings("unchecked")
    private Event_Model(Parcel in){
        creator = in.readString();
        name = in.readString();
        originalName = this.name;
        desc = in.readString();
        month1 = in.readString();
        day1 = in.readString();
        month2 = in.readString();
        day2 = in.readString();
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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String name){
        this.originalName = name;
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

    public String getDay1(){
        return day1;
    }

    public void setDay1(String day1){
        this.day1 = day1;
    }

    public String getMonth1(){
        return month1;
    }

    public void setMonth1(String month1){
        this.month1 = month1;
    }

    public String getMonth2(){
        return month2;
    }

    public void setMonth2(String month2){
        this.month2 = month2;
    }

    public String getDay2(){
        return day2;
    }

    public void setDay2(String day2){
        this.day2 = day2;
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

    @Override
    public int describeContents() {
        return 0;
    }

    // Creates a Parcel of which an instance of the Parcelable class is created from.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creator);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(month1);
        dest.writeString(day1);
        dest.writeString(month2);
        dest.writeString(day2);
        dest.writeInt(privacy);
        dest.writeList(tags);
        dest.writeList(primes);
        dest.writeInt(attending);
        dest.writeInt(invited);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
