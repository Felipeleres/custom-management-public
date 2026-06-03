package com.felipeleres.customermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CustomermanagementApplication implements CommandLineRunner {


	@Autowired
	PasswordEncoder pass;

	public static void main(String[] args) {
		SpringApplication.run(CustomermanagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println(pass.matches("Ffuh-941%@54546","$2a$10$FO3uZLz8cpaSy5xY58Z/d.0vM4lNbmkDsnsCa9LwxckdsPBS7tnuu"));

	}
}
