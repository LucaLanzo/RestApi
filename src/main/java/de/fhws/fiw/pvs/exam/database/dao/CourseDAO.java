package de.fhws.fiw.pvs.exam.database.dao;

import de.fhws.fiw.pvs.exam.resources.Course;
import java.util.List;

/***
 * By Luca Lanzo
 */


public interface CourseDAO {
    List<Course> getAll(int offset, int size);
    List<Course> getByName(String courseName, int offset, int size);
    Course getById(String id);
    void insertInto(Course newCourse);
    void update(Course updatedCourse, String id);
    void delete(String id);

    boolean isNotInDatabase(String id);
}
