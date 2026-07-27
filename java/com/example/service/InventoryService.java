package com.example.service;

import com.example.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryService {
    private final ConcurrentHashMap<Integer, Product> products = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public InventoryService() {
        add("Widget", 9.99, 100);
        add("Gadget", 24.99, 50);
        add("Doohickey", 4.99, 200);
    }

    public List<Product> listAll() {
        return new ArrayList<>(products.values());
    }

    public Product getById(int id) {
        return products.get(id);
    }

    public Product add(String name, double price, int quantity) {
        int id = nextId.getAndIncrement();
        Product product = new Product(id, name, price, quantity);
        products.put(id, product);
        return product;
    }
}
