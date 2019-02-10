/*
 * A model class for a Group.
 *
 * Change the default constructor to only require mandatory fields.
 */
package apps.raymond.friendswholift.Groups;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;

public class GroupBase implements Parcelable {

    private String name;
    private String description;
    private String owner;
    private String visibility;
    private String invite;
    private String imageURI;
    private List<String> tags;
    private byte[] photo;
    private List<GroupEvent> events;
    private byte[] byteArray;

    // Empty constructor as required by FireBase.
    public GroupBase() {
    }

    public GroupBase(String name, String description, String owner, String visibility, String invite, String imageURI){
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.visibility = visibility;
        this.invite = invite;
        this.imageURI = imageURI;
    }

    public GroupBase(Parcel in){
        this.name = in.readString();
        this.description = in.readString();
        this.owner = in.readString();
        this.visibility = in.readString();
        this.invite = in.readString();
        this.imageURI = in.readString();
    }

    public static final Parcelable.Creator<GroupBase> CREATOR = new Parcelable.Creator<GroupBase>(){
        @Override
        public GroupBase createFromParcel(Parcel source) {
            return new GroupBase(source);
        }

        @Override
        public GroupBase[] newArray(int size) {
            return new GroupBase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getOwner(){
        return owner;
    }

    public String getVisibility(){
        return visibility;
    }

    public String getInvite(){
        return invite;
    }

    public String getImageURI(){
        return imageURI;
    }

    public void setByteArray(byte[] byteArray){
        this.byteArray = byteArray;
        //notifyAll();
    }

    public byte[] getByteArray(){
        return byteArray;
    }

    public List<String> getTags(){
        return tags;
    }

    public List<GroupEvent> getEvents(){
        return events;
    }
}