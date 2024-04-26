package dojo.supermarket.model;

import java.util.Objects;

public record ReceiptItem(Product product, double quantity, double price, double totalPrice) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceiptItem that)) return false;
        return Double.compare(that.price, price) == 0 &&
                Double.compare(that.totalPrice, totalPrice) == 0 &&
                Double.compare(that.quantity, quantity) == 0 &&
                Objects.equals(product, that.product);
    }

}
