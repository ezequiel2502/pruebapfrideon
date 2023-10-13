package com.example.sesionconfirebase;

import java.util.List;

public class User {

    private String name;
    private List<String> createdRoutes;
    private List<String> createdEvents;
    private List<String> confirmedEvents;

    public User()
    {}

    public User(String name, List<String> createdRoutes, List<String> createdEvents, List<String> confirmedEvents, List<String> pendingConfirmationEvents) {
        this.name = name;
        this.createdRoutes = createdRoutes;
        this.createdEvents = createdEvents;
        this.confirmedEvents = confirmedEvents;
        this.pendingConfirmationEvents = pendingConfirmationEvents;
    }

    private List<String> pendingConfirmationEvents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCreatedRoutes() {
        return createdRoutes;
    }

    public void setCreatedRoutes(List<String> createdRoutes) {
        this.createdRoutes = createdRoutes;
    }

    public List<String> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(List<String> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public List<String> getConfirmedEvents() {
        return confirmedEvents;
    }

    public void setConfirmedEvents(List<String> confirmedEvents) {
        this.confirmedEvents = confirmedEvents;
    }

    public List<String> getPendingConfirmationEvents() {
        return pendingConfirmationEvents;
    }

    public void setPendingConfirmationEvents(List<String> pendingConfirmationEvents) {
        this.pendingConfirmationEvents = pendingConfirmationEvents;
    }
}
