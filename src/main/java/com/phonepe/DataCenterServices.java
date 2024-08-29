package com.phonepe;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DataCenterServices implements DataCenter{
    private Map<String, List<Resource>> resourcesByInstanceType;
    private Queue<Task> waitingQueue;
    private List<Task> runningTasks;
    private ExecutorService executorService;
    private Map<String,Task> taskMap;

    private Lock lock;

    public DataCenterServices() {
        resourcesByInstanceType = new HashMap<>();
        waitingQueue = new ConcurrentLinkedQueue<>();
        runningTasks = new CopyOnWriteArrayList<>(); // TO handle concurrency issue
        executorService = Executors.newFixedThreadPool(5); // Thread pool size
        taskMap=new HashMap<>();
        lock = new ReentrantLock();
    }

    public String addResource(String type, Resource resource) throws DataCenterResourceException {
        try{
            resourcesByInstanceType.putIfAbsent(type, new ArrayList<>());
            resourcesByInstanceType.get(type).add(resource);
        }catch (ConcurrentModificationException | IllegalArgumentException ex){
            throw new DataCenterResourceException("Exception occurred while adding resource: "+ex.getMessage());
        }
        return "Resource with ID: "+ resource.getId() +" added Successfully";
    }

    public String deleteResource(String type, String resourceId) throws DataCenterResourceException {
            try{
                resourcesByInstanceType.get(type).removeIf(resource -> resource.getId().equals(resourceId));
            }catch (ConcurrentModificationException | IllegalArgumentException ex){
                throw new DataCenterResourceException("Exception occurred while adding resource: "+ex.getMessage());
            }
            String message="Resource with ID: "+resourceId+" type "+type+" Deleted Successfully ";
           System.out.println(message);
        return message;
    }

    public List<Resource> getAvailableResources(String type, int minCpuConfiguration) {

        List<Resource> availableResources = new ArrayList<>();
        if (resourcesByInstanceType.containsKey(type)) {
            for (Resource resource : resourcesByInstanceType.get(type)) {
                if (!resource.isAllocated() && resource.getCpuConfiguration() >= minCpuConfiguration) {
                    availableResources.add(resource);
                }
            }
        }else{
            for (Map.Entry<String,List<Resource>> entry : resourcesByInstanceType.entrySet()) {
                List<Resource> resourceList=entry.getValue();
                for (Resource resource : resourceList) {
                    if (!resource.isAllocated() && resource.getCpuConfiguration() >= minCpuConfiguration) {
                        availableResources.add(resource);
                    }
                }
            }
        }
        System.out.println("Available resources "+availableResources);
        return availableResources;
    }

    public void submitTask(Task task) throws DataCenterResourceException {
        //To handle concurrency issue, im locking it so that only single thread access it
       lock.lock();
        try{
                //Empty instance so that it will take all avaalable resources
            List<Resource> availableResources = getAvailableResources("", task.getRequiredCpuConfiguration());
            Resource allocatedResource = allocateResource(availableResources,task);

            if (!availableResources.isEmpty() && allocatedResource!=null) {
                System.out.println("Started: "+task.getId());
                task.allocateResource(allocatedResource);
                task.setStartTime(LocalDateTime.now());
                allocatedResource.allocate();
                runningTasks.add(task);
                executeTask(task);
                runningTasks.remove(task);

                taskMap.put(task.getId(),task);
                System.out.println("Completed: "+task.getId());
            } else {
                waitingQueue.offer(task);
            }
        }catch (Exception ex){
            throw new DataCenterResourceException("Error while executing task");
        }
        lock.unlock();
    }

    public Resource allocateResource(List<Resource> availableResources,Task task) {
        if(availableResources!=null && availableResources.size()>0){
            //Comparator which compares and check cpu core if it is equal then it compares using price
            Collections.sort(availableResources, (a,b)->{
                if(a.getCpuConfiguration()==b.getCpuConfiguration()){
                   return Double.compare(a.getPrice(),b.getPrice());
                }else{
                    return Integer.compare(a.getCpuConfiguration(),b.getCpuConfiguration());
                }
            });

            for(Resource resource:availableResources){
                if(resource.getCpuConfiguration()>=task.getRequiredCpuConfiguration()){
                    return resource;
                }
            }
        }
        return null;
    }

    public void executeTask(Task task) throws DataCenterResourceException {
        // Task execution
        task.setStatus(Status.STARTED);
        try {
            task.setStatus(Status.RUNNING);
            Thread.sleep(new Random().nextInt(5) + 1000); //  execution time between 1-6 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        task.setEndTime(LocalDateTime.now());
        //De allocate once the task complete, here one task may get many resources according to future scope
        for(Resource resource:task.getAllocatedResources()){
            resource.deallocate();
        }

        task.setStatus(Status.COMPLETED);
        // Check if there are waiting tasks and allocate resources to them
        if (!waitingQueue.isEmpty()) {
            Task nextTask = waitingQueue.poll();
            submitTask(nextTask);
        }
    }

    //Check the info about given task ID
    @Override
    public Task checkTaskStatus(String taskId) throws DataCenterResourceException {
        if(taskMap.containsKey(taskId)){
           return taskMap.get(taskId);
        }
        System.out.println("Task not available for task Id " + taskId);
        return null;
    }

    //Get all resources based on instance
    public List<Resource> getAllocatedResources(String instanceType){
            List<Resource> resourceList =new ArrayList<>();
            for(Resource resource :resourcesByInstanceType.get(instanceType)){
                if(resource.isAllocated()){
                    resourceList.add(resource);
                }
            }
            return resourceList;
    }
}