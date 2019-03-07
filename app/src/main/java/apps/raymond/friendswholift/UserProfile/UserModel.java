/*
 * RayN. 3/5/2019
 * email: email field for user
 * visibility: field that determines if this user to searchable when creating connections
 *
 * ToDo:
 * Add fields during registration for display name. ALTNERNATIVELY, ON LOG IN POP A USER FORM FOR USER TO SET UP THEIR ACCOUNT!!!!!!
 */

package apps.raymond.friendswholift.UserProfile;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String email;
    private String visibility;

    public UserModel(){}

    public UserModel(String email, String visibility){
        this.email = email;
        this.visibility = visibility;
    }

    public UserModel(Parcel in){
        this.email = in.readString();
        this.visibility = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.visibility);
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
