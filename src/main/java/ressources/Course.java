package ressources;

import org.bson.codecs.pojo.annotations.BsonId;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Course {
    @BsonId
    private String _id;
    private String name;

    public Course() {
    }

    public Course(String name) {
        this.name = name;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
