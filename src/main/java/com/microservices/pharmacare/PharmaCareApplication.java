package com.microservices.pharmacare;

import com.microservices.pharmacare.dao.entities.Pharmacien;
import com.microservices.pharmacare.dao.repository.PharmacienRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PharmaCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmaCareApplication.class, args);
	}

	@Bean
	public CommandLineRunner preloadAdmin(PharmacienRepository pharmacienRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (pharmacienRepository.findByEmail("admin@example.com").isEmpty()) {
				Pharmacien admin = new Pharmacien();
				admin.setEmail("admin@example.com");
				admin.setMotDePasse(passwordEncoder.encode("admin123"));
				admin.setUsername("Admin");
				pharmacienRepository.save(admin);
				System.out.println("Admin Pharmacien account created: admin@example.com / admin123");
			} else {
				System.out.println("Admin Pharmacien account already exists.");
			}
		};
	}

}
