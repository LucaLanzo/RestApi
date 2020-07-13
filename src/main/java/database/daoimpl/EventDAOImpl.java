package database.daoimpl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import database.dao.EventDAO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import resources.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    // READ
    @Override
    public List<Event> getAll(int offset, int size) {
        List<Event> allEvents = new ArrayList<>();
        for (Event event : collection.find().skip(offset).limit(size)) {
            allEvents.add(event);
        }
        return allEvents;
    }

    // READ
    @Override
    public List<Event> getByStartTime(String startTime, int offset, int size) {
        List<Event> allEvents = new ArrayList<>();
        for (Event event : collection.find(Filters.eq("startTime", startTime)).skip(offset).limit(size)) {
            allEvents.add(event);
        }
        return allEvents;
    }

    // READ
    @Override
    public List<Event> getByEndTime(String endTime, int offset, int size) {
        List<Event> allEvents = new ArrayList<>();
        for (Event event : collection.find(Filters.eq("endTime", endTime)).skip(offset).limit(size)) {
            allEvents.add(event);
        }
        return allEvents;
    }

    // READ
    @Override
    public List<Event> getByTimeframe(String startTime, String endTime, String courseId, int offset, int size) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");

        try {
            Date startTimeDate = sdf.parse(startTime);
            Date endTimeDate = sdf.parse(endTime);

            // if startTime is later than endTime
            if (startTimeDate.compareTo(endTimeDate) > 0) {
                return new ArrayList<>();
            // if startTime is the same time as the endTime
            } else if (startTimeDate.compareTo(endTimeDate) == 0) {
                if (!courseId.equals("")) {
                    return getSameTimesWithSpecificCourse(startTime, endTime, courseId, offset, size);
                } else {
                    return getSameTimes(startTime, endTime, offset, size);
                }

            // if startTime is sooner than endTime
            } else {
                List<Event> allEvents;
                if (!courseId.equals("")) {
                    allEvents = getByAssociatedCourse(courseId, offset, size);
                } else {
                    allEvents = getAll(offset, size);
                }

                List<Event> allEventsWithRightTimes = new ArrayList<>();
                for (Event event : allEvents) {
                    Date startTimeDateFoundEvent = sdf.parse(event.getStartTime());
                    Date endTimeDateFoundEvent = sdf.parse(event.getEndTime());

                    // The startTime of event is on or after the query startTime but before the query endTime
                    boolean startTimeInTimeframe = startTimeDateFoundEvent.compareTo(startTimeDate) >= 0
                            && startTimeDateFoundEvent.compareTo(endTimeDate) < 0;

                    // The endTime of event is on or before the query endTime but after the query startTime
                    boolean endTimeInTimeFrame = endTimeDateFoundEvent.compareTo(endTimeDate) <= 0
                            && endTimeDateFoundEvent.compareTo(startTimeDate) > 0;

                    // If the startTime and endTime are both in query timeframe add the event to the list
                    if (startTimeInTimeframe && endTimeInTimeFrame) {
                        allEventsWithRightTimes.add(event);
                    }
                }
                return allEventsWithRightTimes;
            }
        } catch (ParseException e) {
            // As the startTime/endTime come from user input all the way through to database level a wrong input could
            // invoke a ParseException. To not break the server with 500 just return an empty ArrayList back to the
            // service as the service just handles it as "no Events found" and returns 404.
            return new ArrayList<>();
        }
    }


    // READ
    @Override
    public List<Event> getSameTimes(String startTime, String endTime, int offset, int size) {
        List<Event> allEvents = new ArrayList<>();

        for (Event event : collection.find(Filters.and(Filters.eq("startTime", startTime),
                Filters.eq("endTime", endTime))).skip(offset).limit(size)) {
            allEvents.add(event);
        }

        return allEvents;
    }


    // READ
    @Override
    public List<Event> getSameTimesWithSpecificCourse(String startTime, String endTime, String courseId, int offset,
                                                      int size) {
        List<Event> allEvents = new ArrayList<>();

        for (Event event : collection.find(Filters.and(Filters.eq("startTime", startTime),
                Filters.eq("endTime", endTime),
                Filters.eq("courseId", courseId))).skip(offset).limit(size)) {
            allEvents.add(event);
        }

        return allEvents;
    }


    // READ
    @Override
    public List<Event> getByAssociatedCourse(String courseLink, int offset, int size) {
        List<Event> allEventsWithSpecificCourse = new ArrayList<>();
        for (Event event : collection.find(Filters.eq("courseId", courseLink)).skip(offset).limit(size)) {
            allEventsWithSpecificCourse.add(event);
        }
        return allEventsWithSpecificCourse;
    }

    // READ
    @Override
    public Event getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    // CREATE
    @Override
    public void insertInto(Event newEvent) {
        collection.insertOne(newEvent);
    }

    // UPDATE
    @Override
    public void update(Event updatedEvent, String id) {
        collection.replaceOne(Filters.eq("_id", id), updatedEvent);
    }

    // DELETE
    @Override
    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }


    @Override
    public boolean isNotInDatabase(String id) {
        Event document = getById(id);
        return document == null;
    }


    @Override
    public boolean startIsAfterEndOrWrongFormat(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");
        try {
            Date startTimeDate = sdf.parse(startTime);
            Date endTimeDate = sdf.parse(endTime);

            // if startTime is later than endTime
            return startTimeDate.compareTo(endTimeDate) > 0;
        } catch (ParseException e) {
            return true;
        }
    }
}
