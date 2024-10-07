package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DiaryRepositoryTest {

  @Autowired
  private DiaryRepository diaryRepository;

  @Test
  public void testFindAllByDate() {
    // Given: 테스트용 데이터 생성 및 저장
    LocalDate testDate = LocalDate.of(2024, 7, 2);

    Diary diary1 = new Diary("Clouds", "04n", 288.89, "안녕하세요2", testDate);
    Diary diary2 = new Diary("Clouds", "04n", 288.89, "안녕하세요2", testDate);

    diaryRepository.save(diary1);
    diaryRepository.save(diary2);

    // When: 해당 날짜로 일기를 조회
    List<Diary> diaries = diaryRepository.findAllByDate(testDate);

    // Then: 데이터가 잘 조회되었는지 확인
    assertNotNull(diaries);  // null이 아니어야 함
    assertFalse(diaries.isEmpty());  // 비어있지 않아야 함
    assertEquals(2, diaries.size());  // 데이터는 2건이 있어야 함
    assertEquals("안녕하세요2", diaries.get(0).getText());  // 데이터 내용 확인
  }
}