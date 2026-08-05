package com.VsmartEngine.MediaJungle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

	@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
	@EnableAsync
	// GDPR-TASK-17: enables the @Scheduled data-retention job in DataRetentionScheduler,
	// which purges/anonymises personal data once it is no longer needed (Art. 5(1)(e)).
	@EnableScheduling
	public class MediaSpringApplication {
		public static void main(String[] args) {
			SpringApplication.run(MediaSpringApplication.class, args);
		}
}
