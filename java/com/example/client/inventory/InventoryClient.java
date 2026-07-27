package com.example.client.inventory;

import com.example.model.Product;
import com.example.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class InventoryClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String baseUrl;

    public InventoryClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Product> listProducts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonUtil.fromJson(response.body(), new TypeToken<List<Product>>() {}.getType());
    }

    public Product getProduct(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/product?id=" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Server returned " + response.statusCode() + ": " + response.body());
        }
        return JsonUtil.fromJson(response.body(), Product.class);
    }

    public Product createProduct(String name, double price, int quantity) throws Exception {
        String body = String.format("{\"name\":\"%s\",\"price\":%.2f,\"quantity\":%d}", name, price, quantity);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/product"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Server returned " + response.statusCode() + ": " + response.body());
        }
        return JsonUtil.fromJson(response.body(), Product.class);
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "http://localhost:8081";

        InventoryClient client = new InventoryClient(host);

        System.out.println("=== Listing all products ===");
        for (Product p : client.listProducts()) {
            printProduct(p);
        }

        System.out.println("\n=== Creating a new product ===");
        Product created = client.createProduct("Thingamajig", 14.99, 75);
        printProduct(created);

        System.out.println("\n=== Fetching product by id " + created.getId() + " ===");
        Product fetched = client.getProduct(created.getId());
        printProduct(fetched);

        System.out.println("\n=== Updated product list ===");
        for (Product p : client.listProducts()) {
            printProduct(p);
        }
    }

    private static void printProduct(Product p) {
        System.out.printf("  [%d] %s - $%.2f (qty: %d)%n", p.getId(), p.getName(), p.getPrice(), p.getQuantity());
    }
}
