package de.fhws.fiw.pvs.exam;

import de.fhws.fiw.pvs.exam.database.DAOFactory;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import de.fhws.fiw.pvs.exam.resources.Event;

public class test {
    public static void main(String[] args) {
        EventDAO eventDatabase = DAOFactory.createEventDAO();
        Event event = new Event();
        System.out.println(eventDatabase.startIsAfterEndOrWrongFormat(event.getStartTime(), event.getEndTime()));
    }
}
