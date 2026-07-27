package com.example.client.greeter;

import com.example.model.Greeting;
import com.example.util.JsonUtil;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GreeterClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;

    public GreeterClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Greeting greet(String name) throws Exception {
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/?name=" + encoded))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Server returned " + response.statusCode() + ": " + response.body());
        }
        return JsonUtil.fromJson(response.body(), Greeting.class);
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "http://localhost:8080";
        String name = args.length > 1 ? args[1] : "Bazel";

        GreeterClient client = new GreeterClient(host);
        Greeting greeting = client.greet(name);

        System.out.println("Name:      " + greeting.getName());
        System.out.println("Message:   " + greeting.getMessage());
        System.out.println("Timestamp: " + greeting.getTimestamp());
    }
}
