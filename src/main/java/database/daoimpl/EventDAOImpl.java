package database.daoimpl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import database.dao.EventDAO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import resources.Course;
import resources.Event;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class EventDAOImpl implements EventDAO {
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
    protected MongoCollection<Event> collection;


    public EventDAOImpl(String collectionName, Class<Event> className) {
        this.collection = database.getCollection(collectionName, className);
    }


    public List<Event> getAll(int offset, int size) {
        List<Event> allEvents = new ArrayList<>();
        for (Event event : collection.find().skip(offset).limit(size)) {
            allEvents.add(event);
        }
        return allEvents;
    }


    public List<Event> getByDate(int date) {
        return null;
    }


    public List<Event> getByTime(int startTime, int endTime) {
        return null;
    }


    public List<Event> getByTimeframe(int startTime, int endTime) {
        return null;
    }


    public List<Event> getByAssociatedCourse(String courseLink, int offset, int size) {
        List<Event> allEventsWithSpecificCourse = new ArrayList<>();
        for (Event event : collection.find(Filters.eq("course", courseLink)).skip(offset).limit(size)) {
            allEventsWithSpecificCourse.add(event);
        }
        return allEventsWithSpecificCourse;
    }


    public Event getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public void insertInto(Event newEvent) {
        collection.insertOne(newEvent);
    }

    public void update(Event updatedEvent, String id) {
        collection.replaceOne(Filters.eq("_id", id), updatedEvent);
    }

    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }


    public boolean isNotInDatabase(String id) {
        Event document = getById(id);
        return document == null;
    }

    public int getAmountOfResources(String courseId) {
        return getByAssociatedCourse(courseId, 0, 0).size();
    }

    public int getAmountOfResources() {
        return (int) collection.countDocuments();
    }
}
