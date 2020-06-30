package database.daoimpl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import database.dao.CourseDAO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import resources.Course;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class CourseDAOImpl implements CourseDAO {
    protected ConnectionString connectionString = new ConnectionString("mongodb://admin:adminpassword@localhost:27017");

    protected CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    protected CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            pojoCodecRegistry);
    protected MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();
    protected MongoClient mongoClient = MongoClients.create(clientSettings);

    protected MongoDatabase database = mongoClient.getDatabase("softSkillsDatabase");
    protected MongoCollection<Course> collection;


    public CourseDAOImpl(String collectionName, Class<Course> className) {
        this.collection = database.getCollection(collectionName, className);
    }


    public List<Course> getAll(int offset, int size) {
        List<Course> allCourses = new ArrayList<>();
        try (MongoCursor<Course> cursor = collection.find().skip(offset).limit(size).iterator()) {
            while(cursor.hasNext()) {
                allCourses.add(cursor.next());
            }
        }
        return allCourses;
    }

    public List<Course> getByName(String name, int offset, int size) {
        MongoCursor<Course> cursor = collection.find(Filters.eq("courseName", name)).skip(offset).limit(size).cursor();
        List<Course> allFoundCourses = new ArrayList<>();
        while(cursor.hasNext()) {
            allFoundCourses.add(cursor.next());
        }
        return allFoundCourses;
    }

    public Course getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public void insertInto(Course newCourse) {
        collection.insertOne(newCourse);
    }

    public void update(Course updatedCourse, String id) {
        collection.replaceOne(Filters.eq("_id", id), updatedCourse);
    }

    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }


    public boolean isNotInDatabase(String id) {
        Course course = getById(id);
        return course == null;
    }

    public int getAmountOfResources(String courseName) {
        return (int) collection.countDocuments(Filters.eq("courseName", courseName));
    }

    public int getAmountOfResources() {
        return (int) collection.countDocuments();
    }
}
