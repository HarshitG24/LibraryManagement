package com.example.distributedsystems.distributed.systems.controller;

import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/employee")
public class EmployeeController {

  @Autowired
  private EmployeeService employeeService;


  @GetMapping("/hello")
  public ResponseEntity<String> hello() {
    return new ResponseEntity<>("Hello World", HttpStatus.OK);
  }

  @GetMapping("/allEmployees")
  public ResponseEntity<List<Employee>> listUsers() {
    List<Employee> users = employeeService.listAllEmployee();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PostMapping("/createEmployee")
  public ResponseEntity<Object> createEmployee(@RequestBody Employee e) {
    employeeService.createEmployee(e);
    return new ResponseEntity<>(HttpStatus.OK);
//        return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PostMapping("/deleteAll")
  public ResponseEntity<Object> delete() {
//        employeeService.delete
//        return new ResponseEntity<>(HttpStatus.OK);
//        return new ResponseEntity<>(users, HttpStatus.OK);

    employeeService.deleteAllData();
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }
}
