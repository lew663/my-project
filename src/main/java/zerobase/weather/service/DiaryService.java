package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.CreateDiaryRequestDto;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiaryService {

  @Value("${openweathermap.key}")
  private String apiKey;

  private final DiaryRepository diaryRepository;

  public void createDiary(CreateDiaryRequestDto requestDto) {

    // 데이터 받아오기
    String weatherData = getWeatherString();
    // 데이터 파싱하기
    Map<String, Object> parsedWeather = parseWeather(weatherData);
    // 파싱된 데이터 + 일기 값 db에 저장
    Diary diary = new Diary(
        (String) parsedWeather.get("main"),
        (String) parsedWeather.get("icon"),
        (Double) parsedWeather.get("temp"),
        requestDto.getText(),
        requestDto.getDate()
    );
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