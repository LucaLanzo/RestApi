package api;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.dao.EventDAO;
import database.daoimpl.EventDAOImpl;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import resources.Course;
import resources.Event;

import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class Test {
    protected static EventDAO eventDatabase;

    public static void main(String[] args) {
         eventDatabase = new EventDAOImpl("events", Event.class);
         List<Event> events = eventDatabase.getAll(0, 100);
         for (Event e : events) {
             System.out.println(e.getCourseId());
         }
    }
}
