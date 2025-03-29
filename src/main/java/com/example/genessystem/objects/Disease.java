package com.example.genessystem.objects;

public class Disease {
    private String name;
    private double weight;

    public Disease(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }
}

