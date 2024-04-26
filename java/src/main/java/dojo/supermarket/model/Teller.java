package dojo.supermarket.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teller {

    private final SupermarketCatalog catalog;
    private final Map<Product, Offer> offers = new HashMap<>();

    public Teller(SupermarketCatalog catalog) {
        this.catalog = catalog;
    }

    public void addSpecialOffer(SpecialOfferType offerType, Product product, double argument) {
        offers.put(product, new Offer(offerType, product, argument));
    }

    public void addBundleOffer(Product product, Product otherProduct, double discount) {
        offers.put(product, new Offer(SpecialOfferType.BUNDLE, otherProduct, discount));
    }

    public Receipt checksOutArticlesFrom(ShoppingCart theCart) {
        Receipt receipt = new Receipt();
        addProductsToReceipt(theCart.getItems(), receipt);
        addDiscountsToReceipt(theCart.productQuantities(), receipt);
        return receipt;
    }

    private void addDiscountsToReceipt(Map<Product, Double> productsWithCount, Receipt receipt) {
        productsWithCount.keySet().stream()
                .filter(offers::containsKey)
                .forEach(product -> addDiscountToReceiptForProduct(receipt, product, productsWithCount.get(product)));
    }

    private void addDiscountToReceiptForProduct(Receipt receipt, Product product, double quantity) {
        Offer offer = offers.get(product);
        double unitPrice = catalog.getUnitPrice(product);
        int quantityAsInt = (int) quantity;
        switch (offer.offerType) {
            case THREE_FOR_TWO -> {
                int productsNeeded = 3;
                if (quantityAsInt >= productsNeeded) {
                    int possibleOffers = quantityAsInt / productsNeeded;
                    double discountAmount = quantity * unitPrice - ((possibleOffers * 2 * unitPrice) + quantityAsInt % productsNeeded * unitPrice);
                    Discount discount = new Discount(product, "3 for 2", -discountAmount);
                    receipt.addDiscount(discount);
                }
            }
            case TWO_FOR_AMOUNT -> {
                int productsNeeded = 2;
                calculateAndAddDiscount(receipt, product, quantity, offer, unitPrice, quantityAsInt, productsNeeded);
            }
            case FIVE_FOR_AMOUNT -> {
                int productsNeeded = 5;
                calculateAndAddDiscount(receipt, product, quantity, offer, unitPrice, quantityAsInt, productsNeeded);
            }
            case TEN_PERCENT_DISCOUNT -> {
                double discountAmount = quantity * unitPrice * offer.argument / 100.0;
                Discount discount = new Discount(product, offer.argument + "% off", -discountAmount);
                receipt.addDiscount(discount);
            }
        }
    }

    private void addProductsToReceipt(List<ProductQuantity> productQuantities, Receipt receipt) {
        for (ProductQuantity productQuantity : productQuantities) {
            Product product = productQuantity.getProduct();
            double quantity = productQuantity.getQuantity();
            double unitPrice = catalog.getUnitPrice(product);
            double price = quantity * unitPrice;
            receipt.addProduct(product, quantity, unitPrice, price);
        }
    }

    private void calculateAndAddDiscount(Receipt receipt, Product p, double quantity, Offer offer, double unitPrice, int quantityAsInt, int productsNeeded) {
        if (quantityAsInt >= productsNeeded) {
            int possibleOffers = quantityAsInt / productsNeeded;
            double discountAmount = quantity * unitPrice - (offer.argument * possibleOffers + quantityAsInt % productsNeeded * unitPrice);
            Discount discount = new Discount(p, productsNeeded + " for " + offer.argument, -discountAmount);
            receipt.addDiscount(discount);
        }
    }
}
