package com.gymmick.smartgym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD

=======
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.*;

@EnableAsync
@EnableJpaAuditing
@EnableScheduling
>>>>>>> b369122 (refactor: remove Redis configuration and related properties from environment and application files)
@SpringBootApplication
public class SmartgymApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartgymApplication.class, args);
	}

}
