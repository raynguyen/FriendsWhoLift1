
package apps.raymond.kinect.Events;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//ToDo: Consider implementing hashcode to individually identify event without comparing strings.
public class Event_Model implements Parcelable{
    private String creator, name, desc;
    private double lat, lng;
    private int privacy;
    private List<String> tags, primes;
    private String address = "Location: TBD";
    private int attending = 1;
    private int invited;
    private long long1, long2; //long1 should be a mandatory field that is mutable.

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
                       ArrayList<String> primes, int invited, long long1, long long2){
        this.creator = creator;
        this.name = name;
        this.desc = desc;
        this.privacy = privacy;
        this.tags = tags;
        this.primes = primes;
        this.invited = invited;
        this.long1 = long1;
        this.long2 = long2;
    }

    public Event_Model(String creator, String name, String desc, int privacy, ArrayList<String> tags,
                       ArrayList<String> primes, int invited, String address, double lat, double lng,
                       long long1, long long2) {
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
        this.long2 = long2;
    }

    @SuppressWarnings("unchecked")
    private Event_Model(Parcel in){
        creator = in.readString();
        name = in.readString();
        desc = in.readString();
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

    public void setLong1(long long1){
        this.long1 = long1;
    }

    public long getLong1(){
        return long1;
    }

    public void setLong2(long long2){
        this.long2 = long2;
    }

    public long getLong2(){
        return long2;
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
