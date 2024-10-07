package zerobase.weather.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.CreateDiaryRequestDto;
import zerobase.weather.dto.ReadDiariesRequestDto;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

  @Value("${openweathermap.key}")
  private String apiKey;

  private final DiaryRepository diaryRepository;
  private final DateWeatherRepository dateWeatherRepository;

  @Transactional
  public void createDiary(CreateDiaryRequestDto requestDto) {
    log.info("Started to create diary for date: {}", requestDto.getDate());
    DateWeather dateWeather = getDateWeatherForDate(requestDto.getDate());
    Diary diary = buildDiaryFromData(dateWeather, requestDto);
    saveDiary(diary);
    log.info("Diary created successfully for date: {}", requestDto.getDate());
  }

  @Transactional
  public DateWeather getDateWeatherForDate(LocalDate date) {
    log.debug("Fetching DateWeather for date: {}", date);
    List<DateWeather> data = dateWeatherRepository.findAllByDate(date);

    if (!data.isEmpty()) {
      log.debug("Found existing weather data for date: {}", date);
      return data.get(0);
    }
    log.debug("No weather data found for date: {}, fetching from API", date);
    return fetchAndSaveWeatherData();
  }

  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
  public List<Diary> readDiary(LocalDate date) {
    log.debug("Reading diary for date: {}", date);
    return diaryRepository.findAllByDate(date);
  }

  @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
  public List<Diary> readDiaries(ReadDiariesRequestDto requestDto) {
    log.debug("Reading diaries between {} and {}", requestDto.getStartDate(), requestDto.getEndDate());
    return diaryRepository.findAllByDateBetween(requestDto.getStartDate(), requestDto.getEndDate());
  }

  @Transactional
  public void updateDiary(CreateDiaryRequestDto requestDto) {
    log.info("Updating diary for date: {}", requestDto.getDate());
    Diary diary = diaryRepository.getFirstByDate(requestDto.getDate());
    diary.updateDiary(requestDto.getDate(), requestDto.getText());
    log.info("Diary updated successfully for date: {}", requestDto.getDate());
  }

  @Transactional
  public void deleteDiary(LocalDate date) {
    log.info("Deleting all diaries for date: {}", date);
    diaryRepository.deleteAllByDate(date);
    log.info("All diaries deleted for date: {}", date);
  }

  @Transactional
  @Scheduled(cron = "0 0 1 * * *")
  public void saveWeatherData() {
    log.info("Scheduled task to save weather data started");
    fetchAndSaveWeatherData();
    log.info("Scheduled task to save weather data finished");
  }

  @Transactional
  private DateWeather fetchAndSaveWeatherData() {
    log.debug("Fetching and saving weather data from API");
    Map<String, Object> weatherData = fetchAndParseWeatherData();
    DateWeather dateWeather = buildDateWeatherFromData(weatherData);
    return dateWeatherRepository.save(dateWeather);
  }

  private Map<String, Object> fetchAndParseWeatherData() {
    String weatherData = getWeatherString();
    return parseWeather(weatherData);
  }

  private Diary buildDiaryFromData(DateWeather dateWeather, CreateDiaryRequestDto requestDto) {
    return new Diary(
        dateWeather.getWeather(),
        dateWeather.getIcon(),
        dateWeather.getTemperature(),
        requestDto.getText(),
        requestDto.getDate()
    );
  }

  private DateWeather buildDateWeatherFromData(Map<String, Object> weatherData) {
    return new DateWeather(
        LocalDate.now(),
        (String) weatherData.get("main"),
        (String) weatherData.get("icon"),
        (Double) weatherData.get("temp")
    );
  }

  private void saveDiary(Diary diary) {
    diaryRepository.save(diary);
  }

  private String getWeatherString() {
    String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
    try {
      URL url = new URL(apiUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int responseCode = connection.getResponseCode();
      BufferedReader br;
      if (responseCode == 200) {
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }
      br.close();
      return response.toString();
    } catch (Exception e) {
      log.error("Failed to get weather data from API", e);
      return "failed to get response";
    }
  }

  private Map<String, Object> parseWeather(String jsonString) {
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    try {
      jsonObject = (JSONObject) jsonParser.parse(jsonString);
    } catch (ParseException e) {
      log.error("Failed to parse weather data", e);
      throw new RuntimeException(e);
    }

    Map<String, Object> resultMap = new HashMap<>();
    JSONObject mainData = (JSONObject) jsonObject.get("main");
    resultMap.put("temp", mainData.get("temp"));

    JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
    if (weatherArray != null && !weatherArray.isEmpty()) {
      JSONObject weatherData = (JSONObject) weatherArray.get(0);
      resultMap.put("main", weatherData.get("main"));
      resultMap.put("icon", weatherData.get("icon"));
    }

    return resultMap;
  }
}
