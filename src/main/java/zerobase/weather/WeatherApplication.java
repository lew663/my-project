package zerobase.weather;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().directory("./.env").load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(WeatherApplication.class, args);
	}

}
