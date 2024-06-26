// Andrew ID - kdhara
// Name - Kshtij Dhara
package ds.project4;

import java.io.*;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;


@WebServlet(name = "jokeServlet", urlPatterns = {"/random-joke", "/joke-by-category", "/login", "/create-user", "/user-jokes", "/dashboard"})
public class jokeServlet extends HttpServlet {

  private static final String BASE_URL = "https://api.chucknorris.io/jokes/random";
  private static final Logger logger = Logger.getLogger(jokeServlet.class.getName());
  private static final String connectionString = "mongodb+srv://kdhara:kdhara@project4task1.dex95no.mongodb.net/?retryWrites=true&w=majority&appName=Project4Task1";
  private static final int shift = 3;
  Map<String, String> user = new HashMap<>();

  static String username;
  static String password;
  Gson gson = new Gson();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("application/json");

    String urlPattern = request.getRequestURI().substring(request.getContextPath().length());
    logger.log(Level.INFO, "Received request for URL pattern: " + urlPattern);

    String jokeJson;
    PrintWriter out = response.getWriter();

    // Handle requests based on URL pattern
    switch (urlPattern) {
      case "/random-joke":
        jokeJson = getJoke(BASE_URL, username);
        out.println(jokeJson);
        response.getWriter().write(jokeJson);
        logRequest(request,jokeJson,urlPattern,200);
        break;
      case "/joke-by-category":
        String category = request.getParameter("category");
        jokeJson = getJoke(BASE_URL + "?category=" + category, username);
        response.getWriter().write(jokeJson);
        logRequest(request,jokeJson,urlPattern,200);
        out.println(jokeJson);
        break;
      case "/create-user":
        username = request.getParameter("username");
        password = request.getParameter("password");
        String createdUsername = create_user(username, password);

        CreateUserResponse createUserResponse = new CreateUserResponse();
        if (createdUsername.equals("Error: Username already in use")) {
          createUserResponse.setError("Username already in use");
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
          createUserResponse.setUsername(createdUsername);
        }
        response.getWriter().write(gson.toJson(createUserResponse));
        break;
      case "/login":
        username = request.getParameter("username");
        password = request.getParameter("password");
        String loggedInUsername = login_user(username, password);
        Gson gson = new Gson();
        LoginResponse loginResponse = new LoginResponse();
        if (loggedInUsername != null) {
          loginResponse.setUsername(loggedInUsername);
        } else {
          loginResponse.setError("Invalid username or password");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        response.getWriter().write(gson.toJson(loginResponse));
        break;
      case "/user-jokes":
        List<Document> userJokes = getUserJokes(username);
        // Convert the list of documents to a JSON string and write it to the response
        String userJokesJson = userJokes.stream()
                .map(Document::toJson)
                .collect(Collectors.joining(",", "[", "]"));
        response.getWriter().write(userJokesJson.toString());
        logRequest(request,userJokesJson,urlPattern,200);
        break;
      case "/dashboard":
        List<Document> allLogs = getAllLogs();
        long userCount = getUsersCount();
        Map<String, Integer> urlPatternCount = countUrlPatterns();
        long jokesCount = getJokesCount();
        System.out.println(urlPatternCount);
        request.setAttribute("allLogs", allLogs);
        request.setAttribute("allUsers", userCount);
        request.setAttribute("urlpatternCount", urlPatternCount);
        request.setAttribute("jokesCount", jokesCount);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/dashboard.jsp");
        dispatcher.forward(request, response);
        break;

      default:
        jokeJson = "{\"error\": \"Invalid request\"}";
        logger.log(Level.WARNING, "Invalid request received");
        logRequest(request,jokeJson,urlPattern,404);
    }
  }

  private String getJoke(String urlString, String username) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Accept", "application/json");

    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      String jokeJson = br.lines().collect(Collectors.joining("\n"));
      saveJokeToDatabase(jokeJson, username);
      return jokeJson;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error fetching joke: " + e.getMessage(), e);
      throw new RuntimeException("Error fetching joke: " + e.getMessage());
    } finally {
      conn.disconnect();
    }
  }

  private void saveJokeToDatabase(String jokeJson, String username) {
    MongoClient mongoClient = MongoClients.create(connectionString);
    MongoDatabase database = mongoClient.getDatabase("distributed_systems");
    MongoCollection<Document> collection = database.getCollection("jokes");

    // Parse the jokeJson and extract the relevant information
    Document jokeDocument = Document.parse(jokeJson);
    jokeDocument.append("username", username);

    // Save the joke document to the "jokes" collection
    collection.insertOne(jokeDocument);

    logger.log(Level.INFO, "Joke saved to database for user: {}", username);
    mongoClient.close();
  }

  private String create_user(String username, String password) {
    MongoClient mongoClient = MongoClients.create(connectionString);
    MongoDatabase database = mongoClient.getDatabase("distributed_systems");
    MongoCollection<Document> collection = database.getCollection("user_login");

    // Check if the username already exists
    Document query = new Document("username", username);
    Document existingUser = collection.find(query).first();

    if (existingUser != null) {
      logger.log(Level.WARNING, "Username already in use: {}", username);
      return "Error: Username already in use";
    }

    // Create a new user
    Document document = new Document()
            .append("username", username)
            .append("password", Methods.encode(password, shift));
    collection.insertOne(document);

    logger.log(Level.INFO,"User {} created successfully", username);
    return username;
  }

  private String login_user(String username, String password) {
    MongoClient mongoClient = MongoClients.create(connectionString);
    MongoDatabase database = mongoClient.getDatabase("distributed_systems");
    MongoCollection<Document> collection = database.getCollection("user_login");

    logger.info("Attempting to log in user: " + username);

    Document query = new Document("username", username);
    Document user = collection.find(query).first();

    if (user != null && user.getString("password").equals(Methods.encode(password, shift))) {
      logger.info("User" + username + "logged in successfully");
      return username;
    } else {
      logger.log(Level.SEVERE, "Invalid username or password for user: " + username);
      return null;
    }
    }
  private List<Document> getUserJokes(String username) {
    MongoClient mongoClient = MongoClients.create(connectionString);
    MongoDatabase database = mongoClient.getDatabase("distributed_systems");
    MongoCollection<Document> collection = database.getCollection("jokes");

    logger.log(Level.INFO, "Retrieving jokes for user: {}", username);

    // Find all the jokes associated with the given username
    List<Document> userJokes = collection.find(Filters.eq("username", username)).into(new ArrayList<>());

    logger.log(Level.INFO, "Found "+ userJokes.size() +" jokes for user: " + username);
    mongoClient.close();
    logger.log(Level.INFO, "MongoDB connection closed");
    return userJokes;
  }

  private void logRequest(HttpServletRequest request, String jokeJson, String urlString, int statusCode) {
    MongoClient mongoClient = MongoClients.create(connectionString);
    try {
      MongoDatabase database = mongoClient.getDatabase("distributed_systems");
      MongoCollection<Document> logCollection = database.getCollection("logs");

      Document logEntry = new Document("timestamp", System.currentTimeMillis())
              .append("username", username)
              .append("urlPattern", request.getRequestURI().substring(request.getContextPath().length()))
              .append("requestParameters", request.getParameterMap().toString()) // Converts the parameter map to a string representation
              .append("apiEndpoint", urlString)
              .append("apiResponse", jokeJson)
              .append("statusCode", statusCode);

      logCollection.insertOne(logEntry);
      logger.log(Level.INFO, "Request logged in the database");
    } finally {
      mongoClient.close();
    }
  }
  private List<Document> getAllLogs() {
    MongoClient mongoClient = MongoClients.create(connectionString);
    try {
      MongoDatabase database = mongoClient.getDatabase("distributed_systems");
      MongoCollection<Document> logCollection = database.getCollection("logs");

      // Fetch all the log entries from the "logs" collection
      List<Document> allLogs = logCollection.find().into(new ArrayList<>());

      logger.log(Level.INFO, "Fetched {0} log entries from the database", allLogs.size());
      return allLogs;
    } finally {
      mongoClient.close();
    }
  }

  public long getUsersCount() {
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      MongoDatabase database = mongoClient.getDatabase("distributed_systems");
      MongoCollection<Document> collection = database.getCollection("user_login");

      // Get the count of documents in the collection
      long count = collection.countDocuments();

      logger.log(Level.INFO, "Total number of users: {0}", count);
      return count;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error occurred while getting user count", e);
      return -1; // or throw exception, handle error according to your application's logic
    }
  }

  private Map<String, Integer> countUrlPatterns() {
    MongoClient mongoClient = MongoClients.create(connectionString);
    try {
      MongoDatabase database = mongoClient.getDatabase("distributed_systems");
      MongoCollection<Document> logCollection = database.getCollection("logs");

      // Group by URL pattern and count occurrences using aggregation
      List<Document> pipeline = Arrays.asList(
              new Document("$group", new Document("_id", "$urlPattern").append("count", new Document("$sum", 1)))
      );

      AggregateIterable<Document> result = logCollection.aggregate(pipeline);

      // Map to store URL pattern and count
      Map<String, Integer> urlPatternCounts = new HashMap<>();
      for (Document doc : result) {
        String urlPattern = doc.getString("_id");
        int count = doc.getInteger("count");
        urlPatternCounts.put(urlPattern, count);
      }
      return urlPatternCounts;
    } finally {
      mongoClient.close();
    }
  }
  private long getJokesCount() {
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      MongoDatabase database = mongoClient.getDatabase("distributed_systems");
      MongoCollection<Document> collection = database.getCollection("jokes");

      // Get the count of documents in the collection
      long count = collection.countDocuments();

      logger.log(Level.INFO, "Total number of jokes: {0}", count);
      return count;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error occurred while getting jokes count", e);
      return -1; // or throw exception, handle error according to your application's logic
    }
  }


}
