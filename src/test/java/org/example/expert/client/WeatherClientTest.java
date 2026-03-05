package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class WeatherClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @Mock
    private RestTemplate restTemplate;

    private WeatherClient weatherClient;

    @BeforeEach
    void setUp() {
        given(restTemplateBuilder.build()).willReturn(restTemplate);
        weatherClient = new WeatherClient(restTemplateBuilder);
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
    private final String today = LocalDate.now().format(formatter);

    @Test
    public void 오늘_날씨를_정상적으로_조회한다() {
        // given
        WeatherDto[] weatherArray = {
                new WeatherDto(today, "Sunny"),
                new WeatherDto("01-01", "Cloudy")
        };

        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(weatherArray, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(responseEntity);

        // when
        String result = weatherClient.getTodayWeather();

        // then
        assertEquals("Sunny", result);
    }

    @Test
    public void API_상태코드가_OK가_아니면_에러발생() {
        // given
        WeatherDto[] weatherArray = new WeatherDto[0];

        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(weatherArray, HttpStatus.INTERNAL_SERVER_ERROR);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(responseEntity);

        // when
        ServerException exception = assertThrows(ServerException.class, () -> {
            weatherClient.getTodayWeather();
        });

        // then
        assertTrue(exception.getMessage().contains("날씨 데이터를 가져오는데 실패했습니다. 상태 코드:"));
    }

    @Test
    public void 날씨_데이터가_null이면_에러발생() {
        // given
        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(responseEntity);

        // when
        ServerException exception = assertThrows(ServerException.class, () -> {
            weatherClient.getTodayWeather();
        });

        // then
        assertEquals("날씨 데이터가 없습니다.", exception.getMessage());
    }

    @Test
    public void 날씨_데이터가_비어있으면_에러발생() {
        // given
        WeatherDto[] weatherArray = new WeatherDto[0];

        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(weatherArray, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(responseEntity);

        // when
        ServerException exception = assertThrows(ServerException.class, () -> {
            weatherClient.getTodayWeather();
        });

        // then
        assertEquals("날씨 데이터가 없습니다.", exception.getMessage());
    }

    @Test
    public void 오늘_날짜에_해당하는_날씨_데이터가_없으면_에러발생() {
        // given
        WeatherDto[] weatherArray = {
                new WeatherDto("01-01", "Sunny"),
                new WeatherDto("01-02", "Cloudy")
        };  // 오늘 날짜 없음

        ResponseEntity<WeatherDto[]> responseEntity = new ResponseEntity<>(weatherArray, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class)))
                .willReturn(responseEntity);

        // when
        ServerException exception = assertThrows(ServerException.class, () -> {
            weatherClient.getTodayWeather();
        });

        // then
        assertEquals("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.", exception.getMessage());
    }
}
