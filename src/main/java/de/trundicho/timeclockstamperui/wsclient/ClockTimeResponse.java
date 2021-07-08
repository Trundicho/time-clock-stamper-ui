package de.trundicho.timeclockstamperui.wsclient;

import java.util.List;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(chain = true)
public class ClockTimeResponse {

    private ClockType currentState;
    private String hoursWorkedToday;
    private String overtimeMonth;
    private List<ClockTime> clockTimes;
}
