package ressources;


import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Course {
    private String name;

    public Course() {
        super();
    }

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
