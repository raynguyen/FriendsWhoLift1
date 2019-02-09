/*
 * A model class for a Group.
 *
 * Change the default constructor to only require mandatory fields.
 */
package apps.raymond.friendswholift.Groups;

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
    private String gcsURI;
    private List<String> tags;
    private byte[] photo;
    private List<GroupEvent> events;

    // Empty constructor as required by FireBase.
    public GroupBase() {
    }

    public GroupBase(String name, String description, String owner, String visibility, String invite, List<String> tags){
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.visibility = visibility;
        this.invite = invite;
        this.tags = tags; //Check what I do for this.
    }

    public GroupBase(Parcel in){
        this.name = in.readString();
        this.description = in.readString();
        this.owner = in.readString();
        this.visibility = in.readString();
        this.invite = in.readString();
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

    public List<String> getTags(){
        return tags;
    }

    public String setGcsURI(){
        return gcsURI;
    }

    public String getGcsURI(){
        return gcsURI;
    }

    public void setPhoto(byte[] photo){
        this.photo = photo;
    }

    public byte[] getPhoto(){
        return photo;
    }

    public List<GroupEvent> getEvents(){
        return events;
    }
}
