package de.fhws.fiw.pvs.exam.resources;

import com.owlike.genson.annotation.JsonConverter;
import de.fhws.fiw.pvs.exam.linkconverter.ServerLinkConverter;
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
    @InjectLink(style = InjectLink.Style.ABSOLUTE, value = "/courses/${instance.courseId}", rel = "courseLink",
            type = "application/json")
    private Link courseLink;
    @InjectLink(style = InjectLink.Style.ABSOLUTE, value = "/events/${instance.hashId}", rel = "self",
            type = "application/json")
    private Link self;


    public Event() {}

    public Event(String startTime, String endTime) {
        this.hashId = ObjectId.get().toString();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter: HashId
    public String getHashId() {
        if (hashId == null) {
            setHashId(ObjectId.get().toString());
        }
        return hashId;
    }
    // Setter: HashId
    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    // Getter: StartTime
    public String getStartTime() {
        return startTime;
    }
    // Setter: EndTime
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    // Getter: EndTime
    public String getEndTime() {
        return endTime;
    }
    // Setter: EndTime
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // Getter: CourseId
    public String getCourseId() {
        return courseId;
    }
    // Setter: CourseId
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // Getter: SignedUpStudents
    public Set<String> getSignedUpStudents() {
        return signedUpStudents;
    }
    // Setter: SignedUpStudents
    public void setSignedUpStudents(Set<String> signedUpStudents) {
        this.signedUpStudents = signedUpStudents;
    }

    // Sign up a student by his cn
    public void joinEvent(String cn) {
        this.signedUpStudents.add(cn);
    }
    // Remove a student by his cn
    public void leaveEvent(String cn) {
        this.signedUpStudents.remove(cn);
    }

    // Get the courseLink
    @JsonConverter(ServerLinkConverter.class)
    public Link getCourseLink() {
        return courseLink;
    }

    // Get the resource itself
    @JsonConverter(ServerLinkConverter.class)
    public Link getSelf() {
        return self;
    }
}
