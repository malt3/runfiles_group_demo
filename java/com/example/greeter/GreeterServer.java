package com.example.greeter;

import com.example.service.GreetingService;
import com.example.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class GreeterServer {
    private final GreetingService greetingService = new GreetingService();

    public static void main(String[] args) throws IOException {
        int port = 8080;
        GreeterServer server = new GreeterServer();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", server::handleGreet);
        httpServer.start();

        System.out.println("Greeter service started on port " + port);
    }

    private void handleGreet(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String name = parseQueryParam(exchange.getRequestURI(), "name");
        if (name == null || name.isEmpty()) {
            name = "World";
        }

        String json = JsonUtil.toJson(greetingService.greet(name));
        byte[] response = json.getBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
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
}
