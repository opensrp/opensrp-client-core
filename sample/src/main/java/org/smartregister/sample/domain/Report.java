package org.smartregister.sample.domain;

import org.jetbrains.annotations.NotNull;
import org.smartregister.view.ListContract;

public class Report implements ListContract.Identifiable {

    private String ID;
    private String name;
    private String salary;
    private String age;

    @NotNull
    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
