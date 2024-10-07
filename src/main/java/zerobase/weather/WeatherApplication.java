package zerobase.weather;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class WeatherApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().directory("./.env").load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(WeatherApplication.class, args);
	}

}
