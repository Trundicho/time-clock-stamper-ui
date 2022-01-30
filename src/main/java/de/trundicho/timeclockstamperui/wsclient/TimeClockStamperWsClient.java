package de.trundicho.timeclockstamperui.wsclient;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TimeClockStamperWsClient {

    @Value("${stamp.url}")
    private String stampUrl;
    private final RestTemplate restTemplate;

    public TimeClockStamperWsClient() {
        restTemplate = new RestTemplate();
    }

    public ClockTimeDataDto stampInOrOut() {
        return restTemplate.postForObject(stampUrl + "/stamp/inOrOut", null, ClockTimeDataDto.class);
    }

    public ClockTimeDataDto getCurrentStampState() {
        return restTemplate.getForObject(stampUrl + "/stamp/state", ClockTimeDataDto.class);
    }

    public String getOvertimeMonth(Integer year, Integer month) {
        return restTemplate.getForObject(String.format(stampUrl + "/stamp/state/%s/%s", year, month), String.class);
    }

    public ClockTimeDataDto stamp(LocalTime time) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LocalTime> httpEntity = new HttpEntity<>(time, headers);
        return restTemplate.postForObject(stampUrl + "/stamp/time", httpEntity, ClockTimeDataDto.class);
    }

    public ClockTimeDataDto setTimeStampsToday(List<ClockTimeDto> clockTimeDtos) {
        ClockTimeDataDto timeDto = new ClockTimeDataDto();
        timeDto.setClockTimes(clockTimeDtos);
        return restTemplate.postForObject(stampUrl + "/stamp/today", timeDto, ClockTimeDataDto.class);
    }
}
