/*
 * RayN. 3/5/2019
 * email: email field for user
 * visibility: field that determines if this user to searchable when creating connections
 *
 * ToDo:
 * Add fields during registration for display name. ALTNERNATIVELY, ON LOG IN POP A USER FORM FOR USER TO SET UP THEIR ACCOUNT!!!!!!
 */

package apps.raymond.kinect.ObjectModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *
 */
public class User_Model implements Parcelable {
    public static final String PUBLIC = "public";//Any user can view your profile.
    public static final String PRIVATE = "private";//Connections and associate connections can view your profile.
    public static final String CLOSED = "closed";//Only connections can view your profile.

    //TODO: There should be a URL to FirebaseStorage where hte user profile picture is stored.
    private String email;
    private String visibility = PUBLIC; //Todo: convert from String to int for quicker comparison
    private String name;
    private String name2;
    private int numconnections = 0;
    private int numlocations = 0;
    private int numinterests = 0;

    public User_Model(){}

    public User_Model(String name, String name2, String email){
        this.name = name;
        this.name2 = name2;
        this.email = email;
    }

    public User_Model(Parcel in){
        name = in.readString();
        name2 = in.readString();
        email = in.readString();
        numconnections = in.readInt();
        numlocations = in.readInt();
        numinterests = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new User_Model(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new User_Model[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.name2);
        dest.writeString(this.email);
        dest.writeInt(this.numconnections);
        dest.writeInt(this.numlocations);
        dest.writeInt(this.numinterests);
    }

    public void setName(String name){
        this.name = name;
    }


    public String getName(){
        return name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public void setEmail(String email){
        this.email = email;
    }

    @NonNull
    public String getEmail(){
        return email;
    }

    public void setVisibility(String visibility){
        this.visibility = visibility;
    }

    public String getVisibility(){
        return visibility;
    }

    public void setNumconnections(int i){
        this.numconnections = i;
    }

    public int getNumconnections(){
        return numconnections;
    }

    public void setNumlocations(int i){
        this.numlocations = i;
    }

    public int getNumlocations(){
        return numlocations;
    }

    public void setNuminterests(int i){
        this.numinterests = i;
    }

    public int getNuminterests(){
        return numinterests;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof User_Model)){
            return false;
        }
        return ((User_Model) obj).getEmail().equals(this.getEmail());
    }
}
