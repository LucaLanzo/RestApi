package api;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import resources.Course;
import resources.Event;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class Test {
    protected static ConnectionString connectionString = new ConnectionString("mongodb://admin:adminpassword@localhost:27017");
    protected static CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    protected static CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            pojoCodecRegistry);
    protected static MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();
    protected static MongoClient mongoClient = MongoClients.create(clientSettings);
    protected static MongoDatabase database = mongoClient.getDatabase("softSkillsDatabase");



    public static void main(String[] args) {
        MongoCollection<Course> courseDatabase = database.getCollection("courses", Course.class);
        MongoCollection<Event> eventDatabase = database.getCollection("events", Event.class);

        courseDatabase.drop();
        eventDatabase.drop();
    }
}
