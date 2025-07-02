package br.com.lothus.models;

import java.util.List;

public class Order {
  public String client_identifier;
  public String client_email;
  public String client_name;
  public String client_discord;
  public String internal_id;
  public List<Product> products;

  public Order(String client_identifier, String client_email, String client_name, String client_discord, String internal_id, List<Product> products) {
    this.client_identifier = client_identifier;
    this.client_email = client_email;
    this.client_name = client_name;
    this.client_discord = client_discord;
    this.internal_id = internal_id;
    this.products = products;
  }

  public List<Product> getProducts() {
    return products;
  }

  public String getClientName() {
    return client_name;
  }
}
