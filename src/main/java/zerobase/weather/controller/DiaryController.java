package zerobase.weather.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.dto.CreateDiaryRequestDto;
import zerobase.weather.service.DiaryService;


@RestController
@RequiredArgsConstructor
public class DiaryController {

  private final DiaryService diaryService;

  @PostMapping("/create/diary")
  void createDiary(@RequestBody CreateDiaryRequestDto requestDto) {
    diaryService.createDiary(requestDto);
  }
}
