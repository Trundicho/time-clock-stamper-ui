package de.trundicho.timeclockstamperui;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class Time {

    private String timeStamp;

}
