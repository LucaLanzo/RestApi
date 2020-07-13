package resources;

import com.owlike.genson.annotation.JsonConverter;
import linkconverter.ServerLinkConverter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/***
 * By Luca Lanzo
 */


@XmlRootElement
public class Event {
    @BsonId
    private String hashId;
    // Time formatting: yyyy-MM-dd--HH-mm-ss
    private String startTime;
    // Time formatting: yyyy-MM-dd--HH-mm-ss
    private String endTime;
    private String courseId;
    private Set<String> signedUpStudents;
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


    public Set<String> getSignedUpStudents() {
        return signedUpStudents;
    }

    public void setSignedUpStudents(Set<String> signedUpStudents) {
        this.signedUpStudents = signedUpStudents;
    }

    public void joinEvent(String cn) {
        this.signedUpStudents.add(cn);
    }

    public void leaveEvent(String cn) {
        this.signedUpStudents.remove(cn);
    }

    @JsonConverter(ServerLinkConverter.class)
    public Link getSelf() {
        return self;
    }
}
