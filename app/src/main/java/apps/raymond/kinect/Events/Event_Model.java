package apps.raymond.kinect.Events;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Event_Model implements Parcelable{
    private static final String TAG = "Event_Model.Class";
    public static final int PUBLIC = 0; //Anyone may join the event without prior connections to guests.
    public static final int CLOSED = 1; //Only users with connected guests may join the event.
    public static final int EXCLUSIVE = 2; //Invitation only

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
    private String month1, month2;
    private String day1, day2;
    private int privacy;
    private List<String> tags;
    private int attending;
    private int invited;

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
        attending = 1;
        invited = 0;
    }

    public Event_Model(String creator, String name, String desc, String month1, String day1, String month2, String day2, int privacy,
                       ArrayList<String> tags){
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
        attending = 1;
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
    }
}
