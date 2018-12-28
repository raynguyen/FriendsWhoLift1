package apps.raymond.friendswholift.LiftObject;

public class LiftObject {

    private int id;
    private String type;
    private double weight;
    private String date;

    public LiftObject(int id, String type, Double weight){
        this.id = id;
        this.type = type;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }
}
