package database.dao;

import resources.Event;
import java.util.List;

public interface EventDAO {
    List<Event> getAll(int offset, int size);
    List<Event> getByDate(int date);
    List<Event> getByTime(int startTime, int endTime);
    List<Event> getByTimeframe(int startTime, int endTime);
    List<Event> getByAssociatedCourse(String courseId, int offset, int size);
    Event getById(String id);
    void insertInto(Event document);
    void update(Event updatedDocument, String id);
    void delete(String id);

    boolean isNotInDatabase(String id);
    int getAmountOfResources(String courseId);
    int getAmountOfResources();
}
