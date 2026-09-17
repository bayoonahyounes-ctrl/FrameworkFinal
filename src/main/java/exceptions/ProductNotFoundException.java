package exceptions;

import java.util.List;


public class ProductNotFoundException extends FrameworkException {

    public ProductNotFoundException(String wanted, List<String> available) {
        super("product '" + wanted + "' was not found on the page; available products: " + available);
    }
}
