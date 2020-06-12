package database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class MongoOperations<D> {
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
    protected MongoCollection<D> collection;

    // protected MongoClient mongoClient= MongoClients.create("mongodb://admin:adminpassword@localhost:27017");
    // protected MongoDatabase database = mongoClient.getDatabase("softSkillsDatabase");
    // public MongoCollection<Course> collection;

    public MongoOperations(String collectionName, Class<D> className) {
        this.collection = database.getCollection(collectionName, className);
    }

    public List<D> getAll() {
        List<D> allDocuments = new ArrayList<>();
        try (MongoCursor<D> cursor = collection.find().iterator()) {
            while(cursor.hasNext()) {
                allDocuments.add(cursor.next());
            }
        }
        return allDocuments;
    }

    public D getByName(String name) {
        return collection.find(Filters.eq("name", name)).first();
    }

    public D getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public D insertInto(D document, String name) {
        collection.insertOne(document);
        return collection.find(Filters.eq("name", name)).first();
    }

    public void update(D updatedDocument, String id) {
        collection.replaceOne(Filters.eq("_id", id), updatedDocument);
    }

    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }
}
