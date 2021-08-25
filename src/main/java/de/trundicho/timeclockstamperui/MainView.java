package de.trundicho.timeclockstamperui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.trundicho.timeclockstamperui.wsclient.ClockTime;
import de.trundicho.timeclockstamperui.wsclient.ClockTimeResponse;
import de.trundicho.timeclockstamperui.wsclient.ClockType;
import de.trundicho.timeclockstamperui.wsclient.TimeClockStamperWsClient;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    private static final String CLOCK_IN = "Clock in";
    private static final String CLOCK_OUT = "Clock out";
    private static final int UI_POLL_INTERVAL = 3000;
    @Value("${time.zoneId}")
    private String zoneId = "Europe/Berlin";

    @Autowired
    public MainView(TimeClockStamperWsClient timeClockStamperWsClient) {
        Button stampInOrOutButton = new Button();
        stampInOrOutButton.setIcon(new Image("/button-a.jpg", "StampButton"));
        stampInOrOutButton.getClassNames().add("timeclockbutton");
        HorizontalLayout stampInOrOutHorizontalLayout = new HorizontalLayout();
        stampInOrOutHorizontalLayout.add(stampInOrOutButton);

        Label workedTodayButton = new Label("Worked today");
        HorizontalLayout workedTodayHorizontalLayout = new HorizontalLayout();
        Label workedToadyLabel = new Label();
        workedTodayHorizontalLayout.add(workedTodayButton, workedToadyLabel);

        Label stampStateButton = new Label("State");
        HorizontalLayout stampStateHorizontalLayout = new HorizontalLayout();
        Label stampStateLabel = new Label();
        stampStateHorizontalLayout.add(stampStateButton, stampStateLabel);

        Label overtimeMonthButton = new Label("Overtime current month");
        HorizontalLayout overtimeCurrentMonthLayout = new HorizontalLayout();
        Label overtimeCurrentMonthLabel = new Label();
        overtimeCurrentMonthLayout.add(overtimeMonthButton, overtimeCurrentMonthLabel);

        IntegerField yearField = createIntegerField("Year");
        IntegerField monthField = createIntegerField("Month");
        yearField.setValue(LocalDateTime.now().getYear());
        monthField.setValue(LocalDateTime.now().getMonth().getValue());
        HorizontalLayout overtimePerMonthLayout = new HorizontalLayout();
        overtimePerMonthLayout.add(yearField);
        overtimePerMonthLayout.add(monthField);

        this.add(stampInOrOutHorizontalLayout);
        this.add(stampStateHorizontalLayout);
        this.add(workedTodayHorizontalLayout);
        this.add(overtimeCurrentMonthLayout);

        this.add(overtimePerMonthLayout);
        Label overtimeMonthLabel = new Label();
        this.add(new HorizontalLayout(new Label("Overtime:"), overtimeMonthLabel));
        TimePicker timePicker = new TimePicker();
        timePicker.setStep(Duration.ofMinutes(15));
        Button addClockTimeButton = new Button("Add clock time");
        this.add(new HorizontalLayout(timePicker, addClockTimeButton));
        Grid<Time> grid = new Grid<>(Time.class);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.add(grid);

        //------------------ Initialize -----------------
        ClockTimeResponse currentStampState = timeClockStamperWsClient.getCurrentStampState();

        updateUi(timeClockStamperWsClient, stampInOrOutButton, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel, yearField,
                monthField, overtimeMonthLabel, currentStampState, grid);

        registerListeners(timeClockStamperWsClient, stampInOrOutButton, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel,
                yearField, monthField, overtimeMonthLabel, timePicker, addClockTimeButton, grid);
    }

    private void registerListeners(TimeClockStamperWsClient timeClockStamperWsClient, Button stampInOrOutButton, Label workedToadyLabel,
            Label stampStateLabel, Label overtimeCurrentMonthLabel, IntegerField yearField, IntegerField monthField,
            Label overtimeMonthLabel, TimePicker timePicker, Button addClockTimeButton, Grid<Time> grid) {
        //------------------ LISTENERS -----------------
        stampInOrOutButton.addClickListener(
                (ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> updateUi(timeClockStamperWsClient, stampInOrOutButton,
                        workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel, yearField, monthField, overtimeMonthLabel,
                        timeClockStamperWsClient.stampInOrOut(), grid));

        UI current = UI.getCurrent();
        current.getPage().setTitle("Time Clock");
        current.setPollInterval(UI_POLL_INTERVAL);
        current.addPollListener(componentEvent -> {
            ClockTimeResponse currentStampState12 = timeClockStamperWsClient.getCurrentStampState();
            updateUi(timeClockStamperWsClient, stampInOrOutButton, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel, yearField,
                    monthField, overtimeMonthLabel, currentStampState12, grid);
        });
        addClockTimeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            LocalTime value = timePicker.getValue();
            if (value != null) {
                ClockTimeResponse currentStampState1 = timeClockStamperWsClient.stamp(value);
                updateUi(timeClockStamperWsClient, stampInOrOutButton, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel,
                        yearField, monthField, overtimeMonthLabel, currentStampState1, grid);
            }
        });
    }

    private void updateUi(TimeClockStamperWsClient timeClockStamperWsClient, Button stampInOrOutButton, Label workedToadyLabel,
            Label stampStateLabel, Label overtimeCurrentMonthLabel, IntegerField yearField, IntegerField monthField,
            Label overtimeMonthLabel, ClockTimeResponse currentStampState, Grid<Time> grid) {
        stampInOrOutButton.setText(ClockType.CLOCK_IN.equals(currentStampState.getCurrentState()) ? CLOCK_OUT : CLOCK_IN);
        stampInOrOutButton.setIcon(ClockType.CLOCK_IN.equals(currentStampState.getCurrentState()) ?
                new Image("/button-b.jpg", "StampButtonClockedOut") :
                new Image("/button-a.jpg", "StampButtonClockedIn"));
        updateOvertimeMonth(timeClockStamperWsClient, yearField, monthField, overtimeMonthLabel);
        updateUi(currentStampState, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel);
        List<ClockTime> clockTimes = currentStampState.getClockTimes();

        List<Time> timers = clockTimes.stream().map(this::getTime).collect(Collectors.toList());
        grid.setItems(timers);
    }

    private Time getTime(ClockTime c) {
        ZoneId zone = ZoneId.of(zoneId);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
        return new Time().setTimeStamp(formatter.format(Date.from(c.getDate().atZone(zone).toInstant())));
    }

    private void updateOvertimeMonth(TimeClockStamperWsClient timeClockStamperWsClient, IntegerField yearField, IntegerField monthField,
            Label overtimeMonthLabel) {
        Integer year = yearField.getValue();
        Integer month = monthField.getValue();
        if (year != null && month != null) {
            String overtimeMonth = timeClockStamperWsClient.getOvertimeMonth(year, month);
            overtimeMonthLabel.setText(overtimeMonth);
        } else {
            overtimeMonthLabel.setText("");
        }
    }

    private void updateUi(ClockTimeResponse clockTimeResponse, Label workedToadyLabel, Label stampStateLabel, Label overtimeMonthLabel) {
        workedToadyLabel.setText(clockTimeResponse.getHoursWorkedToday());
        stampStateLabel.setText(clockTimeResponse.getCurrentState().name());
        String overtimeCurrentMonth = clockTimeResponse.getOvertimeMonth();
        overtimeMonthLabel.setText(overtimeCurrentMonth);
    }

    private IntegerField createIntegerField(String name) {
        IntegerField field = new IntegerField();
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.setHelperText(name);
        return field;
    }
}
