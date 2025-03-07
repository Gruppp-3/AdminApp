package com.example.restaurantorderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WorkShift {
    private Long id;
    private String startTime;
    private String endTime;
    private String description;
    private Employee employee;
    private String shiftStatus;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getShiftStatus() { return shiftStatus; }
    public void setShiftStatus(String shiftStatus) { this.shiftStatus = shiftStatus; }

    // Convenience method: returns a full name or just first name
    public String getEmployeeName() {
        if (employee != null) {
            // Adjust as needed: you could combine first and last name
            return employee.getFirstName() + " " + employee.getLastName();
        }
        return "Ej tilldelad";
    }
}