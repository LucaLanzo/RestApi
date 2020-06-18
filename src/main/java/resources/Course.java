package resources;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
    private String events;


    public Course() {}

    public Course(String courseName, String courseDescription, int maximumStudents) {
        this.hashId = ObjectId.get().toString();
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.maximumStudents = maximumStudents;
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


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }


    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }


    public int getMaximumStudents() {
        return maximumStudents;
    }

    public void setMaximumStudents(int maximumStudents) {
        this.maximumStudents = maximumStudents;
    }


    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }
}