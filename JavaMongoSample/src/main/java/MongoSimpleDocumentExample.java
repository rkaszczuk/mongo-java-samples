import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.bitsAllSet;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;

public class MongoSimpleDocumentExample {
    public static void main(String[] args){
        //Utworzenie klienta Mongo z domyślnymi parametrami połączenia (localhost:27017)
        MongoClient mongoClient = MongoClients.create();

        //Utworzenie klienta Mongo z użyciem connection string - https://docs.mongodb.com/manual/reference/connection-string/
        mongoClient = MongoClients.create("mongodb://localhost:27017");

        //Ustawienie kompresji komunikacji (snappy, zstd, zlib)
        //mongoClient = MongoClients.create("mongodb://localhost:27017?compressors=snappy");

        //Połączenie do Replica Set. Dzieki wskazaniu parametru replicaSet nie musimy wskazywać wszystkicych hostów
        //Jeżeli sterownik połączy się z jakimkolwiek z hostów automatycznie pobierze listę instancji ze wskazanego Replica Set
        //mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018/?replicaSet=rs1")

        //Utworzenie instancji bazy danych "testJava"
        MongoDatabase mongoDatabase = mongoClient.getDatabase("testJava");

        //Drop bazy danych
        mongoDatabase.drop();

        //Utworzenie nowych kolekcji
        mongoDatabase.createCollection("coll1");
        mongoDatabase.createCollection("coll2");

        //Listing kolekcji w bazie danych "test"
        var collectionNames = mongoDatabase.listCollectionNames();
        System.out.println("Listing kolekcji 1:");
        collectionNames.forEach(x-> System.out.print(x+ ", "));
        System.out.println("");

        //Drop utworzonych kolekcji
        mongoDatabase.getCollection("coll1").drop();
        mongoDatabase.getCollection("coll2").drop();

        //Ponowny listing (żeby sprawdzić czy usunęło)
        collectionNames = mongoDatabase.listCollectionNames();
        System.out.println("Listing kolekcji 2");
        collectionNames.forEach(x-> System.out.print(x+ ", "));
        System.out.println("");

        //Utworzenie instancji kolekcji "javaUsers"
        var usersCollection = mongoDatabase.getCollection("javaUsers");

        //Wskazanie Read Preference
        //var usersCollection = mongoDatabase.getCollection("javaUsers").withReadPreference(ReadPreference.primary());

        //Utworzenie przykładowych dokumentów
        var user1doc = new Document().append("name", "Ann").append("age", 30);
        var user2doc = Document.parse("{name : \"Jack\", age : 45}");

        //Inserty dokumentów
        usersCollection.insertOne(user1doc);
        usersCollection.insertOne(user2doc);

        //Bulk inserts (w przypadku błędu - przerwie na pierwszym błędnym dokumencie)
        //usersCollection.insertMany(Arrays.asList(user1doc, user2doc));

        //Bulk inserts unordered (w przypadku błędu nie przerwie dodawania)
        //var insertManyOptions = new InsertManyOptions().ordered(false);
        //usersCollection.insertMany(Arrays.asList(user1doc, user2doc), insertManyOptions);

        //Pobranie wszystkich dokumentów z kolekcji users
        var usersCursor = usersCollection.find().batchSize(100);
        System.out.println("Users");
        usersCursor.forEach(x-> System.out.print(x+ ", "));
        System.out.println("");

        //Filtrowanie danych za pomocą dokumentu filtra
        var query = new Document("name", "Jack");
        var usersWithFilter = usersCollection.find(query);
        System.out.println("Users with filter 1");
        usersWithFilter.forEach(x-> System.out.print(x+ ", "));
        System.out.println("");

        //Filtrowanie danych za pomocą filter helper + projekcja samego klucza "name"
        usersWithFilter = usersCollection.find(eq("name", "Jack"))
                .projection(fields(include("name"), excludeId()));
        System.out.println("Users with filter 2");
        usersWithFilter.forEach(x-> System.out.print(x+ ", "));
        System.out.println("");

        //Update age+10 dla wszystkich dokumentów {}
        usersCollection.updateMany(new Document(), inc("age", 10));
        usersCursor = usersCollection.find().batchSize(100);
        System.out.println("Users after update");
        usersCursor.forEach(x-> System.out.print(x+ ", "));
        System.out.println("");

        //Usunięcie wszystkich dokumentów {}
        usersCollection.deleteMany(new Document());
        var usersCount = usersCollection.countDocuments();
        System.out.println("Users after delete");
        System.out.println(usersCount);
    }
}
