package resources;

import com.owlike.genson.annotation.JsonConverter;
import linkconverter.ServerLinkConverter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * By Luca Lanzo
 */


@XmlRootElement
public class Event {
    @BsonId
    private String hashId;
    private String startTime;
    private String endTime;
    private String courseId;
    @InjectLink(style = InjectLink.Style.ABSOLUTE, value = "/events/${instance.hashId}", rel = "self",
            type = "application/json")
    private Link self;

    public Event() {}

    public Event(String startTime, String endTime) {
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


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }


    @JsonConverter(ServerLinkConverter.class)
    public Link getSelf() {
        return self;
    }
}
