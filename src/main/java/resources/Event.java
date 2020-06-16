package resources;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

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
    private Course course;


    public Event() {}

    public Event(int startTime, int endTime) {
        this.hashId = ObjectId.get().toString();
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getHashId() {
        if (this.hashId == null) {
            setHashId(ObjectId.get().toString());
        }
        return this.hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }


    public int getDate() {
        return this.date;
    }

    public void setDate(int date) {
        this.date = date;
    }


    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }


    public int getEndTime() {
        return this.endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
