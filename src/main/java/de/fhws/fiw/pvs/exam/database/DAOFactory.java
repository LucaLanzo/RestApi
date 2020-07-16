package de.fhws.fiw.pvs.exam.database;

import de.fhws.fiw.pvs.exam.database.dao.CourseDAO;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import de.fhws.fiw.pvs.exam.database.daoimpl.CourseDAOImpl;
import de.fhws.fiw.pvs.exam.database.daoimpl.EventDAOImpl;
import de.fhws.fiw.pvs.exam.resources.Course;
import de.fhws.fiw.pvs.exam.resources.Event;

/***
 * By Luca Lanzo
 */


public class DAOFactory {
    public static CourseDAO createCourseDAO() {
        return new CourseDAOImpl("courses", Course.class);
    }

    public static EventDAO createEventDAO() {
        return new EventDAOImpl("events", Event.class);
    }
}
