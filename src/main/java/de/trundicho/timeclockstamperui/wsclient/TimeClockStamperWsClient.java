package de.trundicho.timeclockstamperui.wsclient;

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

    public ClockTimeResponse stampInOrOut() {
        return restTemplate.postForObject(stampUrl + "/stamp/inOrOut", null, ClockTimeResponse.class);
    }

    public ClockTimeResponse getCurrentStampState() {
        return restTemplate.getForObject(stampUrl + "/stamp/state", ClockTimeResponse.class);
    }

}
