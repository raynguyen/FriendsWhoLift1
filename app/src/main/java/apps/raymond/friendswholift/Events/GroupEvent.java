package apps.raymond.friendswholift.Events;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class GroupEvent implements Parcelable {
    private static final String TAG = "GroupEvent.Class";

    public static final Parcelable.Creator<GroupEvent> CREATOR = new Parcelable.Creator<GroupEvent>(){
        @Override
        public GroupEvent createFromParcel(Parcel source) {
            return new GroupEvent(source);
        }

        @Override
        public GroupEvent[] newArray(int size) {
            return new GroupEvent[size];
        }
    };

    private String name;
    private String desc;
    private String month;
    private String day;

    private GroupEvent(Parcel in){
        Log.i(TAG,"Creating GroupEvent instance: " +name + " via Parcel.");
        name = in.readString();
        desc = in.readString();
        month = in.readString();
        day = in.readString();
    }

    public GroupEvent(String name, String desc, String month, String day){
        Log.i(TAG,"Creating GroupEvent instance: " +name + " via parameter passing.");
        this.name = name;
        this.desc = desc;
        this.month = month;
        this.day = day;
    }

    public String getName(){
        return name;
    }

    public String getDesc(){
        return desc;
    }

    public String getDay(){
        return day;
    }

    public String getMonth(){
        return month;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Creates a Parcel of which an instance of the Parcelable class is created from.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i(TAG,"Calling the writeToParcel method.");
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(month);
        dest.writeString(day);
    }
}
