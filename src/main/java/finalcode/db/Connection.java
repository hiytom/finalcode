package finalcode.db;


import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by peng_chao on 15-8-22.
 */
public class Connection {

    public static void test() {
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase("test");
        MongoCollection<Document> collection = database.getCollection("test");
    }


    public static void main(String arg[]) {
        test();
    }
}
