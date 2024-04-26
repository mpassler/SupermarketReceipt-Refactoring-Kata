package dojo.supermarket.model;

import java.util.HashMap;
import java.util.Map;

public class FakeCatalog implements SupermarketCatalog {
    private final Map<Product, Double> prices = new HashMap<>();

    @Override
    public void addProduct(Product product, double price) {
        this.prices.put(product, price);
    }

    @Override
    public double getUnitPrice(Product p) {
        return this.prices.get(p);
    }
}
