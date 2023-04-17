package com.example.distributedsystems.distributed.systems.service;

import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.repository.EmployeeInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

  @Autowired
  private EmployeeInterface employeeInterface;

  public List<Employee> listAllEmployee() {
    return (List<Employee>) employeeInterface.findAll();
  }

  public void createEmployee(Employee e) {
    employeeInterface.save(e);
  }

  public List<Employee> getEmployeeByFName(String name) {
    return employeeInterface.getEmployeeByFirstName(name);
  }

  public List<Employee> getEmployeeByLName(String name) {
    return employeeInterface.findEmployeesByLastNameContaining(name);
  }

  public void deleteAllData() {
    employeeInterface.deleteAll();
  }
}
