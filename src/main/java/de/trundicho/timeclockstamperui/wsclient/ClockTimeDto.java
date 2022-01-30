package de.trundicho.timeclockstamperui.wsclient;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class ClockTimeDto implements Comparable<ClockTimeDto> {
    private LocalDateTime date;
    private Integer pause;

    @Override
    public int compareTo(ClockTimeDto o) {
        return this.date.compareTo(o.date);
    }


}
