import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

    var client = HttpClient.newHttpClient();

    final String jsonBody = """
                       {"login": "%s","password": "%s"}
                       """;

    for (int i = 1; i <= 1000; i++) {
        String login = "user" + i;
        String password = "pass" + i;

        String body = String.format(jsonBody, login, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.printf("Response for %s (status %d): %s%n", login, response.statusCode(), response.body());
        } catch (Exception e) {
            System.err.printf("Error for %s: %s%n", login, e);
        }

        Thread.sleep(50);
    }

/exit