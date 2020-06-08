package database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


public class MongoOp {
    protected static MongoClient mongoClient = MongoClients.create("mongodb://admin:adminpassword@localhost:27017");
    protected static MongoDatabase database = mongoClient.getDatabase("softSkillsDatabase");
    protected static MongoCollection<Document> collection = database.getCollection("courses");


    public static String insertInto(Document doc) {
        collection.insertOne(doc);
        doc = collection.find().first();
        ObjectId objectId = (ObjectId) doc.get("_id");
        return objectId.toString();
    }


    public static String getById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first().toJson();
    }


    public static List<Document> getAll() {
        List<Document> allDocs = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator();) {
            while(cursor.hasNext()) {
                allDocs.add(cursor.next());
            }
        }
        return allDocs;
    }
}
