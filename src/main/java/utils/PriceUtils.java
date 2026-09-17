package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public final class PriceUtils {

    private PriceUtils() {
    }


    public static double parse(String rawPrice) {
        if (rawPrice == null || rawPrice.isBlank()) {
            throw new IllegalArgumentException("price text must not be blank");
        }
        String cleaned = rawPrice.trim().replace("$", "");
        try {
            return new BigDecimal(cleaned).doubleValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("could not parse price from '" + rawPrice + "'", e);
        }
    }

    public static double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
