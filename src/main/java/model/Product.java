package model;

import java.util.Objects;

/**
 * One row of the product catalogue. Immutable - once built it can't
 * drift out of sync with what was read from the page. Comparable by
 * price so a list of products can be sorted the same way the page
 * itself can be sorted, and the two compared directly.
 */
public final class Product implements Comparable<Product> {

    private final String name;
    private final double price;
    private final String description;

    public Product(String name, double price, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("product name must not be blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("product price must not be negative: " + price);
        }
        this.name = name;
        this.price = price;
        this.description = description == null ? "" : description;
    }

    public String name() {
        return name;
    }

    public double price() {
        return price;
    }

    public String description() {
        return description;
    }

    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product other)) {
            return false;
        }
        return Double.compare(price, other.price) == 0
                && name.equals(other.name)
                && description.equals(other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, description);
    }

    @Override
    public String toString() {
        return "Product{name='%s', price=%.2f}".formatted(name, price);
    }
}
