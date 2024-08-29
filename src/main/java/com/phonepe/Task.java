package com.phonepe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Task {
    private String id;
    private int requiredCpuConfiguration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Resource> allocatedResources;

    private Status status;

    public void setId(String id) {
        this.id = id;
    }

    public Task(String id, int requiredCpuConfiguration) {
        this.id = id;
        this.requiredCpuConfiguration = requiredCpuConfiguration;
        this.startTime = null;
        this.endTime = null;
        this.allocatedResources = new ArrayList<>();
        this.status = null;
    }

    public String getId() {
        return id;
    }

    public void setRequiredCpuConfiguration(int requiredCpuConfiguration) {
        this.requiredCpuConfiguration = requiredCpuConfiguration;
    }

    public void setAllocatedResources(List<Resource> allocatedResources) {
        this.allocatedResources = allocatedResources;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRequiredCpuConfiguration() {
        return requiredCpuConfiguration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Resource> getAllocatedResources() {
        return allocatedResources;
    }

    public void allocateResource(Resource resource) {
        allocatedResources.add(resource);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

}