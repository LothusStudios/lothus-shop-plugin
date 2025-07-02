package br.com.lothus.models;

import java.util.List;

public class Product {
    public String id;
    public String name;
    public List<String> commands;

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", commands=" + commands +
                '}';
    }
}
