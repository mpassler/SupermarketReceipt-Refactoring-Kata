package dojo.supermarket.model;

import java.util.Objects;

public record Product(String name, ProductUnit unit) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(name, product.name) &&
                unit == product.unit;
    }

}
