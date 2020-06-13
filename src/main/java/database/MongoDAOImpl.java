package database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class MongoDAOImpl<D> implements MongoDAO<D> {
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


    public MongoDAOImpl(String collectionName, Class<D> className) {
        this.collection = database.getCollection(collectionName, className);
    }

    // To improve the runtime, pagination in this API is done at database level. No collections needed at the service.
    public List<D> getAll(int offset, int size) {
        if (size < 0) size = 0;
        if (offset >= getAmountOfResources()) {
            offset = getAmountOfResources();
        }
        List<D> allDocuments = new ArrayList<>();
        try (MongoCursor<D> cursor = collection.find().skip(offset).limit(size).iterator()) {
            while(cursor.hasNext()) {
                allDocuments.add(cursor.next());
            }
        }
        return allDocuments;
    }

    public List<D> getByName(String name, int offset, int size) {
        if (size < 0) size = 0;
        if (offset >= getAmountOfResources()) {
            offset = getAmountOfResources();
        }
        MongoCursor<D> cursor = collection.find(Filters.eq("name", name)).skip(offset).limit(size).cursor();
        List<D> allFoundDocuments = new ArrayList<>();
        while(cursor.hasNext()) {
            allFoundDocuments.add(cursor.next());
        }
        return allFoundDocuments;
    }

    public D getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public void insertInto(D document) {
        collection.insertOne(document);
    }

    public void update(D updatedDocument, String id) {
        collection.replaceOne(Filters.eq("_id", id), updatedDocument);
    }

    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    public boolean isNotInDatabase(String id) {
        D document = getById(id);
        return document == null;
    }

    public int getAmountOfResources() {
        return (int) collection.countDocuments();
    }
}
