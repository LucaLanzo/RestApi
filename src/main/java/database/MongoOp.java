package database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;


public class MongoOp {
    protected static MongoClient mongoClient = MongoClients.create("mongodb://admin:adminpassword@localhost:27017");
    protected static MongoDatabase database = mongoClient.getDatabase("softSkillsDatabase");
    protected static MongoCollection<Document> collection = database.getCollection("courses");


    public static List<Document> getAll() {
        List<Document> allCourses = new ArrayList<>();
        // Iterate through all courses in the database and add them to an ArrayList
        try (MongoCursor<Document> cursor = collection.find().iterator();) {
            while(cursor.hasNext()) {
                Document currentCourse = cursor.next();
                // For representation purposes don't use the full ObjectId as _id but rather only the hash value
                currentCourse.put("_id", currentCourse.getObjectId("_id").toString());
                allCourses.add(currentCourse);
            }
        }
        return allCourses;
    }


    public static Document getByName(String name) {
        // Search for the course by name in the database
        Document course = collection.find(Filters.eq("name", name)).first();
        // For representation purposes don't use the full ObjectId as _id but rather only the hash value
        course.put("_id", course.getObjectId("_id").toString());
        return course;
    }


    public static Document getById(ObjectId id) {
        // Search for the course by id in the database
        Document course = collection.find(Filters.eq("_id", id)).first();
        // For representation purposes don't use the full ObjectId as _id but rather only the hash value
        course.put("_id", course.getObjectId("_id").toString());
        return course;
    }


    public static String insertInto(Document newCourse, String name) {
        // Insert the new course
        collection.insertOne(newCourse);
        // Get the new course out of the database by name, as we don't know the _id (created by the database) just yet
        newCourse = collection.find(Filters.eq("name", name)).first();
        // Get the _id of the just created course for the POST return URI header "location"
        ObjectId objectId = (ObjectId) newCourse.get("_id");
        return objectId.toString();
    }


    public static void updateCourse(Document updatedCourseInDoc, ObjectId id) {
        // Set the _id of the old course to the new course
        updatedCourseInDoc.put("_id", id);
        // Find the old course from the database and replace it with the new document
        collection.replaceOne(Filters.eq("_id", id), updatedCourseInDoc);
    }


    public static void deleteCourse(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));

    }
}
