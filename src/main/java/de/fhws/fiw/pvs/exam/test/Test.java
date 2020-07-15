package de.fhws.fiw.pvs.exam.test;


import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import de.fhws.fiw.pvs.exam.database.daoimpl.EventDAOImpl;
import de.fhws.fiw.pvs.exam.resources.Event;

import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/***
 * By Luca Lanzo
 */


public class Test {
    protected static EventDAO eventDatabase;

    public static void main(String[] args) {
         eventDatabase = new EventDAOImpl("events", Event.class);
         List<Event> events = eventDatabase.getAll(0, 100);
         for (Event e : events) {
             System.out.println(e.getCourseId());
         }
    }
}
