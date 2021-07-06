package de.trundicho.timeclockstamperui.wsclient;

import java.time.LocalTime;

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

    public ClockTimeResponse stampInOrOut() {
        return restTemplate.postForObject(stampUrl + "/stamp/inOrOut", null, ClockTimeResponse.class);
    }

    public ClockTimeResponse getCurrentStampState() {
        return restTemplate.getForObject(stampUrl + "/stamp/state", ClockTimeResponse.class);
    }

    public String getOvertimeMonth(Integer year, Integer month) {
        return restTemplate.getForObject(String.format(stampUrl + "/stamp/state/%s/%s", year, month), String.class);
    }

    public ClockTimeResponse stamp(LocalTime time) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LocalTime> httpEntity = new HttpEntity<>(time, headers);
        return restTemplate.postForObject(stampUrl + "/stamp/time", httpEntity, ClockTimeResponse.class);
    }
}
