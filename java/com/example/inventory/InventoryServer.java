package com.example.inventory;

import com.example.model.Product;
import com.example.service.InventoryService;
import com.example.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class InventoryServer {
    private final InventoryService inventoryService = new InventoryService();

    public static void main(String[] args) throws IOException {
        int port = 8081;
        InventoryServer server = new InventoryServer();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", server::handleRoot);
        httpServer.start();

        System.out.println("Inventory service started on port " + port);
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/products")) {
            handleListProducts(exchange);
        } else if (path.equals("/product")) {
            handleProduct(exchange);
        } else {
            sendJson(exchange, 404, "{\"error\": \"not found\"}");
        }
    }

    private void handleListProducts(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String json = JsonUtil.toJson(inventoryService.listAll());
        sendJson(exchange, 200, json);
    }

    private void handleProduct(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetProduct(exchange);
                break;
            case "POST":
                handleCreateProduct(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handleGetProduct(HttpExchange exchange) throws IOException {
        String idParam = parseQueryParam(exchange.getRequestURI(), "id");
        if (idParam == null) {
            sendJson(exchange, 400, "{\"error\": \"missing id parameter\"}");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, "{\"error\": \"invalid id\"}");
            return;
        }

        Product product = inventoryService.getById(id);
        if (product == null) {
            sendJson(exchange, 404, "{\"error\": \"not found\"}");
            return;
        }

        sendJson(exchange, 200, JsonUtil.toJson(product));
    }

    private void handleCreateProduct(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        ProductRequest request = JsonUtil.fromJson(body, ProductRequest.class);

        if (request.name == null || request.name.isEmpty()) {
            sendJson(exchange, 400, "{\"error\": \"name is required\"}");
            return;
        }

        Product product = inventoryService.add(request.name, request.price, request.quantity);
        sendJson(exchange, 201, JsonUtil.toJson(product));
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] response = json.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private static String parseQueryParam(URI uri, String key) {
        String query = uri.getQuery();
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return null;
    }

    private static class ProductRequest {
        String name;
        double price;
        int quantity;
    }
}
