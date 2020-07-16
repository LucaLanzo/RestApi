package de.fhws.fiw.pvs.exam.database.dao;

import de.fhws.fiw.pvs.exam.resources.Course;
import java.util.List;

/***
 * By Luca Lanzo
 */


public interface CourseDAO {
    // Get every course
    List<Course> getAll(int offset, int size);
    // Get all courses/a course by name
    List<Course> getByName(String courseName, int offset, int size);
    // Get a course by its id
    Course getById(String id);
    // Insert a new course
    void insertInto(Course newCourse);
    // Update a course
    void update(Course updatedCourse, String id);
    // Delete a course
    void delete(String id);


    // Additional utility methods:

    // Check if a course is not in the database
    boolean isNotInDatabase(String id);
}
