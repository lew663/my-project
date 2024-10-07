package zerobase.weather.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.dto.CreateDiaryRequestDto;
import zerobase.weather.dto.ReadDiariesRequestDto;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DiaryController {

  private final DiaryService diaryService;

  @PostMapping("/create/diary")
  void createDiary(@RequestBody CreateDiaryRequestDto requestDto) {
    diaryService.createDiary(requestDto);
  }

  @GetMapping("/read/diary")
  List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return diaryService.readDiary(date);
  }

  @GetMapping("/read/diaries")
  List<Diary> readDiaries(@RequestBody ReadDiariesRequestDto requestDto) {
    return diaryService.readDiaries(requestDto);
  }

  @PutMapping("/update/diary")
  void updateDiary(@RequestBody CreateDiaryRequestDto requestDto) {
    diaryService.updateDiary(requestDto);
  }

  @DeleteMapping("/delete/diary")
  void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    diaryService.deleteDiary(date);
  }
}
