package com.phonepe;

import java.util.List;

public interface DataCenter {
     String addResource(String type, Resource resource) throws DataCenterResourceException;
     String deleteResource(String type, String resourceId) throws DataCenterResourceException;
    List<Resource> getAvailableResources(String type, int minCpuConfiguration);
    void submitTask(Task task) throws DataCenterResourceException;
    Resource allocateResource(List<Resource> availableResources,Task task);
    void executeTask(Task task) throws DataCenterResourceException;
    Task checkTaskStatus(String taskId) throws DataCenterResourceException;
}
