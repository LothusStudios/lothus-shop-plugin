package br.com.lothus.models;

import java.util.List;

public class PendingOrder {
    public String id;
    public String buyer;
    public String email;
    public String description;
    public double price;
    public long createdAt;
    public long updatedAt;
    public long expiresAt;
    public String status;
    public String delivery;
    public List<Product> commands;

    @Override
    public String toString() {
        return "PendingOrder{" +
                "id='" + id + '\'' +
                ", buyer='" + buyer + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", delivery='" + delivery + '\'' +
                ", commands=" + commands +
                '}';
    }
}
