package zerobase.weather.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity(name = "date_weather")
@NoArgsConstructor
public class DateWeather {

  @Id
  private LocalDate date;
  private String weather;
  private String icon;
  private double temperature;

  public DateWeather(LocalDate date, String weather, String icon, double temperature) {
    this.date = date;
    this.weather = weather;
    this.icon = icon;
    this.temperature = temperature;
  }
}
