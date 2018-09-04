package bdn.quantum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@PropertySource("classpath:application.properties")
@EnableAutoConfiguration
@EntityScan("bdn.quantum.model")
@EnableJpaRepositories("bdn.quantum.repository")
@SpringBootApplication
public class QuantumApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantumApplication.class, args);
	}
}
