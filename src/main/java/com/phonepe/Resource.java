package com.phonepe;

import lombok.ToString;
//The ToString is lombok annotation please add that dependency to view the Resource object in string format
@ToString
class Resource {
    private String id;
    private double price;
    private int cpuConfiguration;
    private boolean isAllocated;

    public Resource(String id, double price, int cpuConfiguration) {
        this.id = id;
        this.price = price;
        this.cpuConfiguration = cpuConfiguration;
        this.isAllocated = false;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public int getCpuConfiguration() {
        return cpuConfiguration;
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    public void allocate() {
        isAllocated = true;
    }

    public void deallocate() {
        isAllocated = false;
    }
}