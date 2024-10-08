package zerobase.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "DIARY CRUD", description = "날씨 일기 API")
public class DiaryController {

  private final DiaryService diaryService;

  @Operation(summary = "일기 작성", description = "새로운 일기를 작성합니다.")
  @PostMapping("/create/diary")
  void createDiary(@RequestBody CreateDiaryRequestDto requestDto) {
    diaryService.createDiary(requestDto);
  }

  @Operation(summary = "일기 조회", description = "날짜에 맞는 일기를 조회합니다.")
  @GetMapping("/read/diary")
  List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return diaryService.readDiary(date);
  }

  @Operation(summary = "일기 조회(기간)", description = "기간동안 작성한 일기를 조회합니다.")
  @GetMapping("/read/diaries")
  List<Diary> readDiaries(@RequestBody ReadDiariesRequestDto requestDto) {
    return diaryService.readDiaries(requestDto);
  }

  @Operation(summary = "일기 수정", description = "일기를 수정합니다.")
  @PutMapping("/update/diary")
  void updateDiary(@RequestBody CreateDiaryRequestDto requestDto) {
    diaryService.updateDiary(requestDto);
  }

  @Operation(summary = "일기 삭제", description = "일기를 삭제합니다.")
  @DeleteMapping("/delete/diary")
  void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    diaryService.deleteDiary(date);
  }
}
