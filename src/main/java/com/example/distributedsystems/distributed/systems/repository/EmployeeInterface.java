package com.example.distributedsystems.distributed.systems.repository;

import com.example.distributedsystems.distributed.systems.model.Employee;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeInterface extends CrudRepository<Employee, Long> {
  List<Employee> findEmployeesByLastNameContaining(String str);

  List<Employee> getEmployeeByFirstName(String str);
}
