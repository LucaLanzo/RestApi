package resources;

import com.owlike.genson.annotation.JsonIgnore;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import javax.xml.bind.annotation.XmlRootElement;

/***
 * By Luca Lanzo
 */


@XmlRootElement
public class Course {
    @BsonId
    private String hashId;
    private String name;

    public Course() {}

    public Course(String name) {
        this.hashId = ObjectId.get().toString();
        this.name = name;
    }

    public String getHashId() {
        if (this.hashId == null) {
            setHashId(ObjectId.get().toString());
        }
        return this.hashId;
    }

    
    public void setHashId(String hash) {
        this.hashId = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}