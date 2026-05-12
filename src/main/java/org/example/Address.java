package org.example;

public class Address {

    private String street;
    private int flat;

    public Address() {
    }

    public Address(String street, int flat) {
        this.street = street;
        this.flat = flat;
    }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public int getFlat() { return flat; }
    public void setFlat(int flat) { this.flat = flat; }

    @Override
    public String toString() {
        return "Address{street='" + street + "', flat=" + flat + "}";
    }
}
