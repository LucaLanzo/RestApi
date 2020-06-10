package database;


import com.mongodb.client.*;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;


public class MongoOperations {
    protected MongoClient mongoClient;
    protected MongoDatabase database;
    protected MongoCollection<Document> collection;


    public MongoOperations(String collectionName) {
        this.mongoClient = MongoClients.create("mongodb://admin:adminpassword@localhost:27017");
        this.database = mongoClient.getDatabase("softSkillsDatabase");
        // MongoDB collections get automatically created if they don't exist when using getCollection("name")
        // and options are set after inserting the first document
        this.collection = database.getCollection(collectionName);
    }

    public List<Document> getAll() {
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

    public Document getByName(String name) {
        // Search for the course by name in the database
        Document course = collection.find(Filters.eq("name", name)).first();
        // For representation purposes don't use the full ObjectId as _id but rather only the hash value
        course.put("_id", course.getObjectId("_id").toString());
        return course;
    }

    public Document getById(ObjectId id) {
        // Search for the course by id in the database
        Document course = collection.find(Filters.eq("_id", id)).first();
        // For representation purposes don't use the full ObjectId as _id but rather only the hash value
        course.put("_id", course.getObjectId("_id").toString());
        return course;
    }

    public String insertInto(Document newCourse, String name) {
        // Insert the new course
        collection.insertOne(newCourse);
        // Get the new course out of the database by name, as we don't know the _id (created by the database) just yet
        newCourse = collection.find(Filters.eq("name", name)).first();
        // Get the _id of the just created course for the POST return URI header "location"
        ObjectId objectId = (ObjectId) newCourse.get("_id");
        return objectId.toString();
    }

    public void updateCourse(Document updatedCourseInDoc, ObjectId id) {
        // Set the _id of the old course to the new course
        updatedCourseInDoc.put("_id", id);
        // Find the old course from the database and replace it with the new document
        collection.replaceOne(Filters.eq("_id", id), updatedCourseInDoc);
    }

    public void deleteCourse(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));

    }
}
