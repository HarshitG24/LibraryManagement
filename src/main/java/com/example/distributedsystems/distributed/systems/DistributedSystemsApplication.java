package com.example.distributedsystems.distributed.systems;

import com.example.distributedsystems.distributed.systems.model.Employee;
import com.example.distributedsystems.distributed.systems.repository.EmployeeInterface;
import com.example.distributedsystems.distributed.systems.repository.ServerInterface;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DistributedSystemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedSystemsApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(EmployeeInterface ei, ServerInterface si){
		return(args -> {
//				insertEmployee(ei);
			System.out.println("server: " + si.findAll());
		});
	}

	private void insertEmployee(EmployeeInterface repo){
//		repo.save(new Employee("harshit", "gajjar"));
//		repo.save(new Employee("abc", "def"));
//		repo.save(new Employee("qaz", "wsx"));
//		repo.save(new Employee("edc", "tgb"));
	}

}
