import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class MongoSimpleAggregationExample {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        MongoCollection moviesCollection = mongoDatabase.getCollection("movies");
        var aggregationResult = moviesCollection.aggregate(Arrays.asList(
                Aggregates.project(Projections.include("directed_by", "revenue")),
                Aggregates.unwind("$directed_by"),
                Aggregates.group("$directed_by", Accumulators.avg("sredniPrzychod", "$revenue.Amount")),
                Aggregates.sort(Sorts.descending("sredniPrzychod"))
        ));
        aggregationResult.forEach(x-> System.out.println(x));

    }
}
