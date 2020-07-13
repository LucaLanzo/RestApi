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


    // READ
    @Override
    public List<Course> getAll(int offset, int size) {
        List<Course> allCourses = new ArrayList<>();
        for (Course course : collection.find().skip(offset).limit(size)) {
            allCourses.add(course);
        }
        return allCourses;
    }

    // READ
    @Override
    public List<Course> getByName(String name, int offset, int size) {
        List<Course> allFoundCourses = new ArrayList<>();
        for (Course course : collection.find(Filters.eq("courseName", name)).skip(offset).limit(size)) {
            allFoundCourses.add(course);
        }
        return allFoundCourses;
    }

    // READ
    @Override
    public Course getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    // CREATE
    @Override
    public void insertInto(Course newCourse) {
        collection.insertOne(newCourse);
    }

    // UPDATE
    @Override
    public void update(Course updatedCourse, String id) {

        collection.replaceOne(Filters.eq("_id", id), updatedCourse);
    }

    // DELETE
    @Override
    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }


    @Override
    public boolean isNotInDatabase(String id) {
        Course course = getById(id);
        return course == null;
    }
}
