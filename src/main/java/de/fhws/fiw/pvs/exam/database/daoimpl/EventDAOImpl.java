package de.fhws.fiw.pvs.exam.database.daoimpl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import de.fhws.fiw.pvs.exam.resources.Event;

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
    public List<Event> getByTimeframe(String startTime, String endTime, int offset, int size) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd--HH:mm:ss");

        try {
            Date startTimeDate = sdf.parse(startTime);
            Date endTimeDate = sdf.parse(endTime);

            // if startTime is later than endTime
            if (startTimeDate.compareTo(endTimeDate) > 0) {
                return new ArrayList<>();
            // if startTime is the same time as the endTime
            } else if (startTimeDate.compareTo(endTimeDate) == 0) {
                return getSameTimes(startTime, endTime, offset, size);

            // if startTime is sooner than endTime
            } else {
                List<Event> allEvents;
                allEvents = getAll(offset, size);

                List<Event> allEventsWithRightTimes = new ArrayList<>();
                for (Event event : allEvents) {
                    Date startTimeDateFoundEvent = sdf.parse(event.getStartTime());
                    Date endTimeDateFoundEvent = sdf.parse(event.getEndTime());

                    // The startTime of event is on or after the query startTime but before the query endTime
                    boolean startTimeInTimeframe = startTimeDateFoundEvent.compareTo(startTimeDate) >= 0 &&
                            startTimeDateFoundEvent.compareTo(endTimeDate) <= 0;

                    // The endTime of event is on or before the query endTime but after the query startTime
                    boolean endTimeInTimeFrame = endTimeDateFoundEvent.compareTo(endTimeDate) <= 0 &&
                            endTimeDateFoundEvent.compareTo(startTimeDate) >= 0;

                    // If the startTime and endTime are both in query timeframe add the event to the list
                    if (startTimeInTimeframe || endTimeInTimeFrame) {
                        allEventsWithRightTimes.add(event);
                    }
                }
                return allEventsWithRightTimes;
            }
        } catch (ParseException e) {
            // As the startTime/endTime come from user input all the way through to de.fhws.fiw.pvs.exam.de.fhws.fiw.pvs.exam.database level a wrong input could
            // invoke a ParseException. To not break the server with 500 just return an empty ArrayList back to the
            // de.fhws.fiw.pvs.exam.service as the de.fhws.fiw.pvs.exam.service just handles it as "no Events found" and returns 404.
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
    public List<Event> filterListForSpecificCourse(List<Event> allEvents, String courseLink) {
        List<Event> allEventsWithSpecificCourse = new ArrayList<>();
        for (Event event : allEvents) {
            if (event.getCourseId().equals(courseLink)) {
                allEventsWithSpecificCourse.add(event);
            }
        }
        return allEventsWithSpecificCourse;
    }


    // READ
    @Override
    public Event getById(String id) {
        return collection.find(Filters.eq("_id", id)).first();
    }


    // READ
    @Override
    public Event getByIdWithSpecificCourse(String id, String courseLink) {
        Event event = collection.find(Filters.eq("_id", id)).first();
        if (event != null && !event.getCourseId().equals(courseLink)) {
            return null;
        }
        return event;
    }


    // CREATE
    @Override
    public void insertInto(Event newEvent) {
        collection.insertOne(newEvent);
    }

    // UPDATE
    @Override
    public void update(Event updatedEvent, String id) {
        Event oldEvent = getById(id);
        if (!oldEvent.getStartTime().equals(updatedEvent.getStartTime())) {
            oldEvent.setStartTime(updatedEvent.getStartTime());
        }
        if (!oldEvent.getEndTime().equals(updatedEvent.getEndTime())) {
            oldEvent.setEndTime(updatedEvent.getEndTime());
        }
        if (!oldEvent.getSignedUpStudents().equals(updatedEvent.getSignedUpStudents())) {
            oldEvent.setSignedUpStudents(updatedEvent.getSignedUpStudents());
        }
        collection.replaceOne(Filters.eq("_id", id), oldEvent);
    }

    // UPDATE
    @Override
    public void signUp(String cn, String id) {
        Event event = getById(id);
        event.joinEvent(cn);
        collection.replaceOne(Filters.eq("_id", id), event);
    }

    // DELETE
    @Override
    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    // DELETE
    @Override
    public void leave(String cn, String id) {
        Event event = getById(id);
        event.leaveEvent(cn);
        collection.replaceOne(Filters.eq("_id", id), event);
    }


    @Override
    public boolean isNotInDatabase(String id) {
        Event document = getById(id);
        return document == null;
    }


    @Override
    public boolean startIsAfterEndOrWrongFormat(String startTime, String endTime) {
        if (startTime.equals("") && endTime.equals("")) return false;

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