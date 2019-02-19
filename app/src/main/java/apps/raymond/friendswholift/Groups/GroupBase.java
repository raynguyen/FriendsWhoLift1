/*
 * A model class for a Group.
 *
 * Change the default constructor to only require mandatory fields.
 */
package apps.raymond.friendswholift.Groups;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupBase implements Parcelable {

    private String name;
    private String description;
    private String owner;
    private String visibility;
    private String invite;
    private String imageURI;
    private byte[] bytes;

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

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getVisibility(){
        return visibility;
    }

    public void setVisibility(String visibility){
        this.visibility = visibility;
    }

    public String getInvite(){
        return invite;
    }

    public void setInvite(String invite){
        this.invite = invite;
    }

    public String getImageURI(){
        return imageURI;
    }

    public void setImageURI(String imageURI){
        this.imageURI = imageURI;
    }

    public byte[] getBytes(){
        return this.bytes;
    }

    public void setBytes(byte[] bytes){
        this.bytes =bytes;
    }
}