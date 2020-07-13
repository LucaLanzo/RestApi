package database.dao;

import resources.Event;
import java.util.List;

/***
 * By Luca Lanzo
 */


public interface EventDAO {
    List<Event> getAll(int offset, int size);
    List<Event> getByStartTime(String startTime, int offset, int size);
    List<Event> getByEndTime(String endTime, int offset, int size);
    List<Event> getByTimeframe(String startTime, String endTime, String courseLink, int offset, int size);
    List<Event> getSameTimes(String startTime, String endTime, int offset, int size);
    List<Event> getSameTimesWithSpecificCourse(String startTime, String endTime, String courseLink, int offset, int size);
    List<Event> getByAssociatedCourse(String courseLink, int offset, int size);
    Event getById(String eventId);
    void insertInto(Event document);
    void update(Event updatedDocument, String eventId);
    void signUp(String cn, String id);
    void delete(String eventId);
    void leave(String cn, String id);

    boolean isNotInDatabase(String eventId);
    boolean startIsAfterEndOrWrongFormat(String startTime, String endTime);
}
