package zerobase.weather.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Diary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String weather;

  private String icon;

  private double temperature;

  private String text;

  private LocalDate date;

  public Diary(String weather, String icon, double temperature, String text, LocalDate date) {
    this.weather = weather;
    this.icon = icon;
    this.temperature = temperature;
    this.text = text;
    this.date = date;
  }
}
