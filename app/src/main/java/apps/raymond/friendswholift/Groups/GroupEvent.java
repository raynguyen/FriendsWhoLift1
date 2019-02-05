package apps.raymond.friendswholift.Groups;

public class GroupEvent {

    private String name;
    private String desc;
    private String month;
    private String day;

    public GroupEvent(String name, String desc, String month, String day){
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

}
