package com.dyad.hrms.models;

public record Employee(String firstName, String lastName) {

    public Employee withLastName(String newLastName) {
        return new Employee(firstName, newLastName);
    }
}
