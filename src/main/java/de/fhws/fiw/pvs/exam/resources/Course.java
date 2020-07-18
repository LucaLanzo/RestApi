package de.fhws.fiw.pvs.exam.resources;

import com.owlike.genson.annotation.JsonConverter;
import de.fhws.fiw.pvs.exam.linkconverter.ServerLinkConverter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlRootElement;

/***
 * By Luca Lanzo
 */

@XmlRootElement
public class Course {
    @BsonId
    private String hashId;
    private String courseName;
    private String courseDescription;
    private int maximumStudents;
    @InjectLink(style = InjectLink.Style.ABSOLUTE, value = "/courses/${instance.hashId}/events", rel = "allEvents",
            type = "application/json")
    private Link allEvents;
    @InjectLink(style = InjectLink.Style.ABSOLUTE, value = "/courses/${instance.hashId}", rel = "self",
            type = "application/json")
    private Link self;


    public Course() {}

    public Course(String courseName, String courseDescription, int maximumStudents) {
        this.hashId = ObjectId.get().toString();
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.maximumStudents = maximumStudents;
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

    // Getter: CourseName
    public String getCourseName() {
        return courseName;
    }
    // Setter: CourseName
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Getter: CourseDescription
    public String getCourseDescription() {
        return courseDescription;
    }
    // Setter: CourseDescription
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    // Getter: MaximumStudents
    public int getMaximumStudents() {
        return maximumStudents;
    }
    // Setter: MaximumStudents
    public void setMaximumStudents(int maximumStudents) {
        this.maximumStudents = maximumStudents;
    }

    // Get the link to events
    @JsonConverter(ServerLinkConverter.class)
    public Link getEvents() {
        return allEvents;
    }


    // Get the resource itself
    @JsonConverter(ServerLinkConverter.class)
    public Link getSelf() {
        return self;
    }
}