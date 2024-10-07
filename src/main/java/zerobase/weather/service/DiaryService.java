package zerobase.weather.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.CreateDiaryRequestDto;
import zerobase.weather.dto.ReadDiariesRequestDto;
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
@Transactional
public class DiaryService {

  @Value("${openweathermap.key}")
  private String apiKey;

  private final DiaryRepository diaryRepository;

  public void createDiary(CreateDiaryRequestDto requestDto) {
    Map<String, Object> weatherData = fetchAndParseWeatherData();
    Diary diary = buildDiaryFromData(weatherData, requestDto);
    saveDiary(diary);
  }

  public List<Diary> readDiary(LocalDate date) {
    return diaryRepository.findAllByDate(date);
  }

  public List<Diary> readDiaries(ReadDiariesRequestDto requestDto) {
    return diaryRepository.findAllByDateBetween(requestDto.getStartDate(), requestDto.getEndDate());
  }

  public void updateDiary(CreateDiaryRequestDto requestDto) {
    Diary diary = diaryRepository.getFirstByDate(requestDto.getDate());
    diary.updateDiary(requestDto.getDate(), requestDto.getText());
  }

  public void deleteDiary(LocalDate date) {
    diaryRepository.deleteAllByDate(date);
  }

  private Map<String, Object> fetchAndParseWeatherData() {
    String weatherData = getWeatherString();
    return parseWeather(weatherData);
  }

  private Diary buildDiaryFromData(Map<String, Object> weatherData, CreateDiaryRequestDto requestDto) {
    return new Diary(
        (String) weatherData.get("main"),
        (String) weatherData.get("icon"),
        (Double) weatherData.get("temp"),
        requestDto.getText(),
        requestDto.getDate()
    );
  }

  private void saveDiary(Diary diary) {
    diaryRepository.save(diary);
  }

  /**
   * openweathermap 에서 날씨 데이터 가져오기
   */
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
      return "failed to get response";
    }

  }

  /**
   * json 데이터 파싱하기
   */
  private Map<String, Object> parseWeather(String jsonString) {
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    try {
      jsonObject = (JSONObject) jsonParser.parse(jsonString);
    } catch (ParseException e) {
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