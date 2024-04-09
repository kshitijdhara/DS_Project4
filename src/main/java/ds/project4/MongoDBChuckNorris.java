// Andrew ID - kdhara
// Name - Kshtij Dhara
package ds.project4;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.Scanner;
import org.bson.Document;

public class MongoDBChuckNorris {
    public static void main(String[] args) {
        // MongoDB connection details
        String connectionString = "mongodb+srv://kdhara:kdhara@project4task1.dex95no.mongodb.net/?retryWrites=true&w=majority&appName=Project4Task1";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("distributed_systems");
        MongoCollection<Document> collection = database.getCollection("user_input_data");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Add a string to MongoDB");
            System.out.println("2. Read all strings from MongoDB");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addStringToMongoDB(collection, scanner);
                    break;
                case 2:
                    readStringsFromMongoDB(collection);
                    break;
                case 3:
                    mongoClient.close();
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void addStringToMongoDB(MongoCollection<Document> collection, Scanner scanner) {
        System.out.println("Enter the ChuckNorris joke to store in MongoDB:");
        String userInput = scanner.nextLine(); // Consume newline character
        userInput = scanner.nextLine(); // Read actual input
        Document document = new Document("joke", userInput);
        collection.insertOne(document);
        System.out.println("Data stored successfully!");
    }

    private static void readStringsFromMongoDB(MongoCollection<Document> collection) {
        System.out.println("All Chuck Norris jokes stored in MongoDB:");
        FindIterable<Document> documents = collection.find();
        MongoCursor<Document> cursor = documents.iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println(doc.getString("joke"));
        }
    }
}
