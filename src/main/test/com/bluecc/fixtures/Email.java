package com.bluecc.fixtures;

/*
CREATE TABLE email(
    id Int32,
    employee_id Int32,
    address text
) engine=Log;
 */
public class Email {
    private Integer id;
    private Integer employeeId;
    private String address;

    public Email() {
    }

    public Email(Integer id, Integer employeeId, String address) {
        this.id = id;
        this.employeeId = employeeId;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", address='" + address + '\'' +
                '}';
    }
}
