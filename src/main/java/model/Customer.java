package model;

/**
 * One row of {@code testdata/customers.csv}. Maps directly onto the
 * three fields the checkout information page asks for.
 */
public final class Customer {

    private final String firstName;
    private final String lastName;
    private final String postalCode;

    public Customer(String firstName, String lastName, String postalCode) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName must not be blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName must not be blank");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("postalCode must not be blank");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String postalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return "Customer{firstName='%s', lastName='%s', postalCode='%s'}"
                .formatted(firstName, lastName, postalCode);
    }
}
