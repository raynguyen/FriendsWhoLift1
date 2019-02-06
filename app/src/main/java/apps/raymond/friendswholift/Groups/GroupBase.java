/*
 * A model class for a Group.
 */
package apps.raymond.friendswholift.Groups;

import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;

public class GroupBase {

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
        this.tags = tags;
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

    public void addEvent(GroupEvent newEvent){
        this.events.add(newEvent);
    }

    public List<GroupEvent> getEvents(){
        return events;
    }
}
