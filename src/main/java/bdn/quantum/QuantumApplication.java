package bdn.quantum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@EntityScan("bdn.quantum.model")
@EnableJpaRepositories("bdn.quantum.repository")
public class QuantumApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantumApplication.class, args);
	}
}
