package api;


import database.MongoOperations;
import org.bson.types.ObjectId;
import ressources.Course;

import java.util.List;

/***
 * By Luca Lanzo
 */


public class Test {
    public static void main(String[] args) {
        MongoOperations<Course> courseDatabase = new MongoOperations<>("courses", Course.class);
        courseDatabase.collection.drop();
    }
}
