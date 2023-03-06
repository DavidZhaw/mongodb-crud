package ch.zhaw;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Scanner;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class App {
    public static void main(String[] args) {
        System.out.println("CRUD Demo");
        Scanner keyScan = new Scanner(System.in);

        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:...");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        // disable logging
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        // configure the client and open db
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("CrudDemo");

        // open collection
        MongoCollection<Document> col = database.getCollection("kontakte");

        // create bson document
        Document kontakt = new Document("_id", new ObjectId());
        kontakt.append("name", "Hans");
        kontakt.append("jahrgang", 1970);
        kontakt.append("stadt", "Zug");

        // write document to db
        InsertOneResult result = col.insertOne(kontakt);
        System.out.println(result.toString());

        System.out.println("Created document. Press Enter to continue");
        keyScan.nextLine();

        // read all documents from a collection
        FindIterable<Document> alleKontakte = col.find();

        // write name of all contacts to terminal
        for (Document d : alleKontakte) {
            System.out.println(d.get("name"));
        }

        // read all contacts from zug
        FindIterable<Document> fromZug = col.find(eq("stadt", "Zug"));

        // cast iterable into an array list
        ArrayList<Document> fromZugList = fromZug.into(new ArrayList<Document>());

        // print length of list
        System.out.println(fromZugList.size());

        // access element from list
        Document first = fromZugList.get(0);
        System.out.println(first.get("jahrgang"));

        System.out.println("Read document. Press Enter to continue");
        keyScan.nextLine();

        // update many
        Bson filter = eq("stadt", "Zug");
        Bson updateOperation = set("stadt", "Winterthur");
        UpdateResult updateResult = col.updateMany(filter, updateOperation);
        System.out.println(updateResult);

        System.out.println("Updated document. Press Enter to continue");
        keyScan.nextLine();

        // delete one
        Bson filter2 = eq("name", "Hans");
        DeleteResult delResult = col.deleteOne(filter2);
        System.out.println(delResult);

        System.out.println("Deleted document. Press Enter to continue");
        keyScan.nextLine();

        Gson gson = new GsonBuilder().create();

        // create POJO
        Kontakt k1 = new Kontakt("Sue", 2000, "Embrach");

        // convert POJO into bson and store it in db
        String k1Json = gson.toJson(k1); // object to json
        Document k1Doc = Document.parse(k1Json); // json to bson
        InsertOneResult resultGson = col.insertOne(k1Doc);
        System.out.println(resultGson);

        System.out.println("POJO written. Press Enter to continue");
        keyScan.nextLine();

        Document fistSueBson = col.find(eq("name", "Sue")).first();
        Kontakt sue = gson.fromJson(fistSueBson.toJson(), Kontakt.class);

        System.out.println(sue.getJahrgang());

        System.out.println("Document retrieved and mapped to POJO. Bye");
        keyScan.close();
    }
}
