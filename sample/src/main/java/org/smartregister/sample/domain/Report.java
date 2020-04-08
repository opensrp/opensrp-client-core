package org.smartregister.sample.domain;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.smartregister.view.ListContract;

/**
 * @author rkodev
 */
public class Report implements ListContract.Identifiable {

    @SerializedName("id")
    private String ID;
    @SerializedName("employee_name")
    private String name;
    @SerializedName("employee_salary")
    private String salary;
    @SerializedName("employee_age")
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
