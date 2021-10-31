import Models.UserModel;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoModelExample {
    public static void main(String[] args){



        MongoClient mongoClient = MongoClients.create();
        MongoDatabase mongoDatabase = mongoClient.getDatabase("testJava");

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoDatabase = mongoDatabase.withCodecRegistry(pojoCodecRegistry);


        MongoCollection<UserModel> usersCollection = mongoDatabase.getCollection("javaUsers", UserModel.class);

        var usr1 = new UserModel();
        usr1.setName("Ann");
        usr1.setAge(30);
        usersCollection.insertOne(usr1);

        var usr2 = new UserModel();
        usr2.setName("Jack");
        usr2.setAge(45);
        usersCollection.insertOne(usr2);

        var usersCursor = usersCollection.find();
        System.out.println("Users:");
        usersCursor.forEach(x->System.out.println(x.getId() + " " + x.getName() + " "+x.getAge() ));



        usersCollection.deleteMany(new Document());
        var usersCount = usersCollection.countDocuments();
        System.out.println("Users after delete");
        System.out.println(usersCount);
    }
}
