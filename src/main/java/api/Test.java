package api;


import com.owlike.genson.Genson;
import database.MongoOp;
import org.bson.Document;
import org.bson.types.ObjectId;
import ressources.Course;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Document> allDocs = MongoOp.getAll();
        for (Document doc : allDocs) {
            System.out.println(doc.toString());
        }
    }
}
