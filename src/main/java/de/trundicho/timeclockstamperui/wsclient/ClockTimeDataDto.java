package de.trundicho.timeclockstamperui.wsclient;

import java.util.List;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class ClockTimeDataDto {

    private ClockType currentState;
    private String hoursWorkedToday;
    private String overtimeMonth;
    private List<ClockTimeDto> clockTimes;
}
