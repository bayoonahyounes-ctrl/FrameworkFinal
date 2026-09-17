package model;

import utils.PriceUtils;

import java.util.List;

/**
 * The money math for a cart, worked out in Java from the {@link Product}
 * list that was read off the page - never read back off the checkout
 * overview page itself. A test builds one of these from the products it
 * knows it added, then compares it against what the page displays.
 */
public final class CartSummary {

    private final List<Product> items;
    private final double taxRate;

    public CartSummary(List<Product> items, double taxRate) {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        if (taxRate < 0) {
            throw new IllegalArgumentException("taxRate must not be negative: " + taxRate);
        }
        this.items = List.copyOf(items);
        this.taxRate = taxRate;
    }

    public double itemTotal() {
        double sum = items.stream().mapToDouble(Product::price).sum();
        return PriceUtils.round(sum);
    }

    public double tax() {
        return PriceUtils.round(itemTotal() * taxRate);
    }

    public double total() {
        return PriceUtils.round(itemTotal() + tax());
    }

    public int itemCount() {
        return items.size();
    }
}
