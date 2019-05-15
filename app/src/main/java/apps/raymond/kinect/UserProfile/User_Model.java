/*
 * RayN. 3/5/2019
 * email: email field for user
 * visibility: field that determines if this user to searchable when creating connections
 *
 * ToDo:
 * Add fields during registration for display name. ALTNERNATIVELY, ON LOG IN POP A USER FORM FOR USER TO SET UP THEIR ACCOUNT!!!!!!
 */

package apps.raymond.kinect.UserProfile;

import android.os.Parcel;
import android.os.Parcelable;

public class User_Model implements Parcelable {
    //TODO: There should be a URL to FirebaseStorage where hte user profile picture is stored.
    private String email;
    private String visibility;
    private String name;


    public User_Model(){}

    public User_Model(String email){
        this.email = email;
    }

    public User_Model(Parcel in){
        this.email = in.readString();
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
        dest.writeString(this.email);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setVisibility(String visibility){
        this.visibility = visibility;
    }

    public String getVisibility(){
        return visibility;
    }

    
}
