package dojo.supermarket.model;

import java.util.*;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, Double> productQuantities = new HashMap<>();

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(Product product) {
        addItemQuantity(product, 1.0);
    }

    Map<Product, Double> productQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    public void addItemQuantity(Product product, double quantity) {
        items.add(new ProductQuantity(product, quantity));
        if (productQuantities.containsKey(product)) {
            productQuantities.put(product, productQuantities.get(product) + quantity);
        } else {
            productQuantities.put(product, quantity);
        }
    }

    void handleOffers(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog) {
        for (Product p : productQuantities().keySet()) {
            double quantity = productQuantities.get(p);
            if (offers.containsKey(p)) {
                Offer offer = offers.get(p);
                double unitPrice = catalog.getUnitPrice(p);
                int quantityAsInt = (int) quantity;
                if (offer.offerType == SpecialOfferType.THREE_FOR_TWO) {
                    int productsNeeded = 3;
                    int possibleOffers = quantityAsInt / productsNeeded;
                    if (quantityAsInt >= productsNeeded) {
                        double discountAmount = quantity * unitPrice - ((possibleOffers * 2 * unitPrice) + quantityAsInt % productsNeeded * unitPrice);
                        Discount discount = new Discount(p, "3 for 2", -discountAmount);
                        receipt.addDiscount(discount);
                    }
                    continue;
                }
                if (offer.offerType == SpecialOfferType.TWO_FOR_AMOUNT) {
                    int productsNeeded = 2;
                    if (quantityAsInt >= productsNeeded) {
                        int possibleOffers = quantityAsInt / productsNeeded;
                        double pricePerUnit = offer.argument * possibleOffers;
                        double theTotal = (quantityAsInt % productsNeeded) * unitPrice;
                        double total = pricePerUnit + theTotal;
                        double discountN = unitPrice * quantity - total;
                        Discount discount = new Discount(p, productsNeeded + " for " + offer.argument, -discountN);
                        receipt.addDiscount(discount);
                    }
                    continue;
                }
                if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT) {
                    int productsNeeded = 5;
                    int possibleOffers = quantityAsInt / productsNeeded;
                    if (quantityAsInt >= productsNeeded) {
                        double discountTotal = unitPrice * quantity - (offer.argument * possibleOffers + quantityAsInt % productsNeeded * unitPrice);
                        Discount discount = new Discount(p, productsNeeded + " for " + offer.argument, -discountTotal);
                        receipt.addDiscount(discount);
                    }
                    continue;
                }
                if (offer.offerType == SpecialOfferType.TEN_PERCENT_DISCOUNT) {
                    Discount discount = new Discount(p, offer.argument + "% off", -quantity * unitPrice * offer.argument / 100.0);
                    receipt.addDiscount(discount);
                }
            }
        }
    }

}
