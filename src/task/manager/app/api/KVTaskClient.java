package task.manager.app.api;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String API_TOKEN;
    private final String address = "http://localhost:8078";

    public KVTaskClient(URL url) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .uri(URI.create(url + "/register"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);
        API_TOKEN = response.body();

    }

    public void put(String key, String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(address + "/save/" + key + "?API_TOKEN=" + API_TOKEN))
                .POST(body)
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        httpClient.send(request, handler);
    }

    public String load(String key) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .GET()
                .uri(URI.create(address + "/load/" + key + "?API_TOKEN=" + API_TOKEN))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);
        return response.body();
    }
}
