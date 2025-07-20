import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    private static List<String> quotes;

    public static void main(String[] args) throws IOException {
        quotes = loadQuotesFromFile("quotes.txt");
        if (quotes.isEmpty()) {
            System.err.println("No quotes found.");
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Serve index.html
        server.createContext("/", exchange -> {
            byte[] html = Files.readAllBytes(Paths.get("index.html"));
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html);
            }
        });

        // API: /api returns a random quote
        server.createContext("/api", exchange -> {
            String quote = getRandomQuote();
            String json = String.format("{\"quote\": \"%s\"}", quote);
            byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });

        server.start();
        System.out.println("Server running at http://localhost:8000");
    }

    private static String getRandomQuote() {
        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }

    private static List<String> loadQuotesFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }
}

