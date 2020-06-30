package resources;

import com.owlike.genson.annotation.JsonConverter;
import linkconverter.ServerLinkConverter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/***
 * By Luca Lanzo
 */


@XmlRootElement
public class Event {
    @BsonId
    private String hashId;
    private int date;
    private int startTime;
    private int endTime;
    private String course;
    @InjectLink(style = InjectLink.Style.ABSOLUTE, value = "/events/${instance.hashId}", rel = "self",
            type = "application/json")
    private Link self;

    public Event() {}

    public Event(int startTime, int endTime) {
        this.hashId = ObjectId.get().toString();
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getHashId() {
        if (hashId == null) {
            setHashId(ObjectId.get().toString());
        }
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }


    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }


    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }


    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }


    @JsonConverter(ServerLinkConverter.class)
    public Link getSelf() {
        return self;
    }
}
