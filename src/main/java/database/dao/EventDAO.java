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
    List<Event> getByTimeframe(String startTime, String endTime, String courseId, int offset, int size);
    List<Event> getSameTimes(String startTime, String endTime, int offset, int size);
    List<Event> getSameTimesWithSpecificCourse(String startTime, String endTime, String courseId, int offset, int size);
    List<Event> getByAssociatedCourse(String courseId, int offset, int size);
    Event getById(String id);
    void insertInto(Event document);
    void update(Event updatedDocument, String id);
    void delete(String id);

    boolean isNotInDatabase(String id);
    boolean startIsAfterEndOrWrongFormat(String startTime, String endTime);
}
