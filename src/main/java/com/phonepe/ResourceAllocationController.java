package com.phonepe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResourceAllocationController {
    public static void main(String[] args) throws DataCenterResourceException {
        DataCenterServices dataCenterServices = new DataCenterServices();

        // Add resources
        try {
            dataCenterServices.addResource("SERVER_INSTANCE_INDIA", new Resource("Server12", 8.0, 10));
            dataCenterServices.addResource("SERVER_INSTANCE_INDIA", new Resource("Server7", 8.0, 10));
            dataCenterServices.addResource("SERVER_INSTANCE", new Resource("Server1", 10.0, 8));
            dataCenterServices.addResource("SERVER_INSTANCE_USA", new Resource("Server2", 12.0, 12));
            dataCenterServices.addResource("SERVER_INSTANCE_INDIA", new Resource("Server3", 8.0, 4));
            dataCenterServices.addResource("SERVER_INSTANCE_SINGAPORE", new Resource("Server4", 8.0, 4));
            dataCenterServices.addResource("SERVER_INSTANCE_SINGAPORE", new Resource("Server10", 8.0, 7));
            dataCenterServices.addResource("SERVER_INSTANCE_SINGAPORE", new Resource("Server9", 8.0, 8));
            dataCenterServices.addResource("SERVER_INSTANCE_SINGAPORE", new Resource("Server8", 8.0, 9));

        } catch (DataCenterResourceException e) {
            throw new RuntimeException(e);
        }

        //Get all resources
        System.out.println(dataCenterServices.getAvailableResources("SERVER_INSTANCE_INDIA",4));
        //Delete resource
        try {
            dataCenterServices.deleteResource("SERVER_INSTANCE_INDIA","Server7");

        } catch (DataCenterResourceException e) {
            throw new RuntimeException(e);
        }
        // Submit tasks in parallel using executor service
        ExecutorService executorService =Executors.newFixedThreadPool(3);
        executorService.submit(()->{

        Task task1 = new Task("Task1", 6);
        Task task2 = new Task("Task2", 10);
        Task task3 = new Task("Task3", 5);
        Task task10 = new Task("Task10", 5);
        Task task11 = new Task("Task11", 5);
        Task task4 = new Task("Task4", 5);

            try {
                dataCenterServices.submitTask(task1);

                //Get allocated Resuorces
                System.out.println("Allocated task for Particular Type");
                List<Resource> resourceList=dataCenterServices.getAllocatedResources("SERVER_INSTANCE_SINGAPORE");
                System.out.println(resourceList);

                dataCenterServices.submitTask(task2);
                dataCenterServices.submitTask(task3);
                dataCenterServices.submitTask(task10);
                dataCenterServices.submitTask(task11);
                dataCenterServices.submitTask(task4);
            } catch (DataCenterResourceException e) {
                throw new RuntimeException(e);
            }
        });
        executorService.shutdown();

        try {
            //To wait for the task to complete
            Thread.sleep(8000);
            Task task=dataCenterServices.checkTaskStatus("Task1");
            if(task!=null)
            System.out.println("Status of Task Id "+task.getId()+" Status " +task.getStatus()+" Start time "+ task.getStartTime()+" End time: "+ task.getEndTime());
        } catch (DataCenterResourceException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
