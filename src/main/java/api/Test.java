package api;


import com.owlike.genson.Genson;
import database.MongoOperations;
import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import org.bson.types.ObjectId;
import ressources.Course;

import javax.ws.rs.core.GenericEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * By Luca Lanzo
 */


public class Test {
    public static void main(String[] args) {
        Genson builder = new Genson();
        Course course = new Course("TestKurs");
        System.out.println(builder.serialize(course));
    }
}
