package com.example.todolist;

//Class represent a task object which is initialise when data is read from the tatabase.
//Each Task object represents a record in the database which each field initialise to the
//associated value in the database
public class Task {
    private String taskID;
    private String description;
    private String startDate;
    private String endDate;
    private String status;

    //Constructor initialise a Task object with the supplied parameters
    public Task(String aTaskId, String aDescription, String aStartDate, String aEndDate, String aStatus) {
        this.taskID = aTaskId;
        this.description = aDescription;
        this.startDate = convertDateFormat(aStartDate);
        this.endDate = convertDateFormat(aEndDate);
        this.status = aStatus;
    }
    //METHOD GETTERS & SETTERS to set and return values of variables
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = convertDateFormat(startDate);
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate =convertDateFormat(endDate);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }



    //convert database date format to UK format DD-MM-YY
    private String convertDateFormat(String aDate){
        if(aDate != null && !aDate.isEmpty()) {
            //Current date format is YY-MM-DD
            String[] dateArray = aDate.split("-");
            //Convert to format DD-MM-YYYY
            String year = dateArray[0];
            String month = dateArray[1];
            String day = dateArray[2];

            return day + "-" + month + "-" + year;
        }
        return "";
    }
}
