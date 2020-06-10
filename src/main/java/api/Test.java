package api;

import com.owlike.genson.Genson;
import database.MongoOperations;
import org.bson.Document;
import org.bson.types.ObjectId;
import ressources.Course;

import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) {
        MongoOperations courseDatabase = new MongoOperations("courses");
        List<Document> allDocs = (ArrayList) courseDatabase.getAll();
        for (Document doc : allDocs) {
            System.out.println(doc.toString());
        }
    }
}
