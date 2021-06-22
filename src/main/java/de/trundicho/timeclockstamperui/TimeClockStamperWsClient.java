package de.trundicho.timeclockstamperui;

import org.springframework.beans.factory.annotation.Value;
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

    public String stampInOrOut() {
        return restTemplate.postForObject(stampUrl + "/stamp/inOrOut", null, String.class);
    }

    public String getHoursWorkedToday() {
        return restTemplate.getForObject(stampUrl + "/stamp/worked/today", String.class);
    }

    public String getCurrentStampState() {
        return restTemplate.getForObject(stampUrl + "/stamp/state", String.class);
    }

    public String getOvertimeMonth() {
        return restTemplate.getForObject(stampUrl + "/stamp/overtime/month", String.class);
    }
}
