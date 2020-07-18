package de.fhws.fiw.pvs.exam.database.dao;

import de.fhws.fiw.pvs.exam.resources.Event;
import java.util.List;

/***
 * By Luca Lanzo
 */


public interface EventDAO {
    // Get every event
    List<Event> getAll(int offset, int size);
    // Get an event by searching for its exact startTime
    List<Event> getByStartTime(String startTime, int offset, int size);
    // Get an event by searching for its exact endTime
    List<Event> getByEndTime(String endTime, int offset, int size);
    // Get all events in between a start- and endTime
    List<Event> getByTimeframe(String startTime, String endTime, int offset, int size);
    // A method for the getByTimeframe method to get all events that have the exact same start- and endTime
    List<Event> getSameTimes(String startTime, String endTime, int offset, int size);
    // Filter a given list of events by a specific courseId
    List<Event> filterListForSpecificCourse(List<Event> allEvents, String courseId);
    // Get an event by its id
    Event getById(String eventId);
    // Get an event that has a specific courseId
    Event getByIdWithSpecificCourse(String id, String courseId);
    // Insert a new event
    void insertInto(Event document);
    // Update an event
    void update(Event updatedDocument, String eventId);
    // Sign a student up to an event by adding his cn
    void signUp(String cn, String id);
    // Delete an event
    void delete(String eventId);
    // Release a student from an event by delete his cn
    void leave(String cn, String id);


    // Additional utility methods:

    // Check if an event is not in the database
    boolean isNotInDatabase(String eventId);
    // Check the start- and endTime if they are not in the proper format.
    boolean startIsAfterEndOrWrongFormat(String startTime, String endTime);
    // Check if start- or endTime is wrong
    boolean timeInWrongFormat(String startTime, String endTime);
}
