package apps.raymond.friendswholift;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Lift implements Parcelable {
    private int id, weight;
    private Date date;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Lift createFromParcel(Parcel in){
            return new Lift(in);
        }

        public Lift[] newArray(int size){
            return new Lift[size];
        }
    };

    public Lift(){
        super();
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getWeight(){
        return weight;
    }

    public Date getDate(){
        return date;
    }

    public int getId(){
        return id;
    }

    private Lift(Parcel in){
        super();
        this.id = in.readInt();
        this.weight = in.readInt();
        this.date = new Date(in.readLong());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Lift other = (Lift) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeInt(getId());
        parcel.writeInt(getWeight());
        parcel.writeLong(getDate().getTime());
    }

    @Override
    public int describeContents(){
        return 0;
    }

}
