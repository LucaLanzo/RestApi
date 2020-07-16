package de.fhws.fiw.pvs.exam.database.daoimpl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import de.fhws.fiw.pvs.exam.database.dao.CourseDAO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import de.fhws.fiw.pvs.exam.resources.Course;

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
    // Get every course
    @Override
    public List<Course> getAll(int offset, int size) {
        List<Course> allCourses = new ArrayList<>();
        for (Course course : collection.find().skip(offset).limit(size)) {
            allCourses.add(course);
        }
        return allCourses;
    }


    // READ
    // Get all courses/a course by names
    @Override
    public List<Course> getByName(String name, int offset, int size) {
        List<Course> allFoundCourses = new ArrayList<>();
        for (Course course : collection.find(Filters.eq("courseName", name)).skip(offset).limit(size)) {
            allFoundCourses.add(course);
        }
        return allFoundCourses;
    }


    // READ
    // Get a course by its id
    @Override
    public Course getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }


    // CREATE
    // Insert a new course
    @Override
    public void insertInto(Course newCourse) {
        collection.insertOne(newCourse);
    }


    // UPDATE
    // Update a course
    @Override
    public void update(Course updatedCourse, String id) {
        Course oldCourse = getById(id);
        if (updatedCourse.getCourseName() != null && !oldCourse.getCourseName().equals(updatedCourse.getCourseName())) {
            oldCourse.setCourseName(updatedCourse.getCourseName());
        }
        if (updatedCourse.getCourseDescription() != null
                && !oldCourse.getCourseDescription().equals(updatedCourse.getCourseDescription())) {
            oldCourse.setCourseDescription(updatedCourse.getCourseDescription());
        }
        if (updatedCourse.getMaximumStudents() > 0
                && oldCourse.getMaximumStudents() != updatedCourse.getMaximumStudents()) {
            oldCourse.setMaximumStudents(updatedCourse.getMaximumStudents());
        }
        collection.replaceOne(Filters.eq("_id", id), oldCourse);
    }


    // DELETE
    // Delete a course
    @Override
    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }



    // Additional utility methods:

    // Check if a course is not in the database
    @Override
    public boolean isNotInDatabase(String id) {
        Course course = getById(id);
        return course == null;
    }
}
