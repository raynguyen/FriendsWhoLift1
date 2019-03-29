package apps.raymond.kinect.Events;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Event_Model implements Parcelable{
    private static final String TAG = "Event_Model.Class";

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

    // Empty constructor as required by FireBase.
    public Event_Model(){
    }

    private String creator;
    private String originalName;
    private String name;
    private String desc;
    private String month;
    private String day;
    private String privacy;
    private List<String> tags;
    private int attenders;
    private int invited;

    @SuppressWarnings("unchecked")
    private Event_Model(Parcel in){
        this.creator = in.readString();
        this.name = in.readString();
        this.originalName = this.name;
        this.desc = in.readString();
        this.month = in.readString();
        this.day = in.readString();
        this.privacy = in.readString();
        this.tags = in.readArrayList(null);
        attenders = 1;
        invited = 0;
    }

    public Event_Model(String creator, String name, String desc, String month, String day, String privacy,
                       ArrayList<String> tags){
        this.creator = creator;
        this.name = name;
        this.originalName = name;
        this.desc = desc;
        this.month = month;
        this.day = day;
        this.privacy = privacy;
        this.tags = tags;
        attenders = 1;
        invited = 0;
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

    public String getDay(){
        return day;
    }

    public void setDay(String day){
        this.day = day;
    }

    public String getMonth(){
        return month;
    }

    public void setMonth(String month){
        this.month = month;
    }

    public String getPrivacy(){
        return privacy;
    }

    public void setPrivacy(String privacy){
        this.privacy = privacy;
    }

    public List<String> getTags(){
        return this.tags;
    }

    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }

    public void setAttenders(int i){
        attenders = i;
    }
    public int getAttenders(){
        return attenders;
    }

    public void setInvited(int i){
        invited = i;
    }

    public int getInvited(){
        return invited;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Creates a Parcel of which an instance of the Parcelable class is created from.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i(TAG,"Calling the writeToParcel method.");
        dest.writeString(creator);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(month);
        dest.writeString(day);
        dest.writeList(tags);
    }
}
