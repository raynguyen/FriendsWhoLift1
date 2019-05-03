package apps.raymond.kinect;

import android.os.Parcel;
import android.os.Parcelable;

public class Message_Model implements Parcelable {

    public static final Parcelable.Creator<Message_Model> CREATOR = new Parcelable.Creator<Message_Model>(){
        @Override
        public Message_Model createFromParcel(Parcel source) {
            return new Message_Model(source);
        }

        @Override
        public Message_Model[] newArray(int size) {
            return new Message_Model[size];
        }

    };

    //private URI author;
    private String author;
    private String message;
    private long timestamp;
    public Message_Model(){
        //Empty constructor
    }

    public Message_Model(Parcel in){
        author = in.readString();
        message = in.readString();
        timestamp = in.readLong();
    }

    public Message_Model(String author, String message, long timestamp){
        this.author = author;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(message);
        dest.writeLong(timestamp);
    }
}
