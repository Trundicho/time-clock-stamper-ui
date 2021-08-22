package de.trundicho.timeclockstamperui;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import de.trundicho.timeclockstamperui.wsclient.ClockTime;
import de.trundicho.timeclockstamperui.wsclient.ClockTimeResponse;
import de.trundicho.timeclockstamperui.wsclient.ClockType;
import de.trundicho.timeclockstamperui.wsclient.TimeClockStamperWsClient;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "about", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    private static final String CLOCK_IN = "Clock in";
    private static final String CLOCK_OUT = "Clock out";

    @Autowired
    public MainView(TimeClockStamperWsClient timeClockStamperWsClient) {
        Button stampInOrOutButton = new Button();
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

        Button updateUiButton = new Button("Update ui");

        IntegerField yearField = new IntegerField();
        IntegerField monthField = new IntegerField();
        yearField.setHelperText("Year");
        monthField.setHelperText("Month");
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
        this.add(updateUiButton);
        TimePicker timePicker = new TimePicker();
        timePicker.setStep(Duration.ofMinutes(15));
        Button addClockTimeButton = new Button("Add clock time");
        this.add(new HorizontalLayout(timePicker, addClockTimeButton));
        Grid<ClockTime> grid = new Grid<>(ClockTime.class);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.add(grid);

        //------------------ Initialize -----------------
        ClockTimeResponse currentStampState = timeClockStamperWsClient.getCurrentStampState();

        updateUi(timeClockStamperWsClient, stampInOrOutButton, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel, yearField,
                monthField, overtimeMonthLabel, currentStampState, grid);

        registerListeners(timeClockStamperWsClient, stampInOrOutButton, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel,
                updateUiButton, yearField, monthField, overtimeMonthLabel, timePicker, addClockTimeButton, grid);
    }

    private void registerListeners(TimeClockStamperWsClient timeClockStamperWsClient, Button stampInOrOutButton, Label workedToadyLabel,
            Label stampStateLabel, Label overtimeCurrentMonthLabel, Button updateUiButton, IntegerField yearField, IntegerField monthField,
            Label overtimeMonthLabel, TimePicker timePicker, Button addClockTimeButton, Grid<ClockTime> grid) {
        //------------------ LISTENERS -----------------
        stampInOrOutButton.addClickListener(
                (ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> updateUi(timeClockStamperWsClient, stampInOrOutButton,
                        workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel, yearField, monthField, overtimeMonthLabel,
                        timeClockStamperWsClient.stampInOrOut(), grid));

        updateUiButton.addClickListener(
                (ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> updateUi(timeClockStamperWsClient, stampInOrOutButton,
                        workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel, yearField, monthField, overtimeMonthLabel,
                        timeClockStamperWsClient.getCurrentStampState(), grid));

        UI current = UI.getCurrent();
        current.setPollInterval(5000);
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
            Label overtimeMonthLabel, ClockTimeResponse currentStampState, Grid<ClockTime> grid) {
        stampInOrOutButton.setText(ClockType.CLOCK_IN.equals(currentStampState.getCurrentState()) ? CLOCK_OUT : CLOCK_IN);
        updateOvertimeMonth(timeClockStamperWsClient, yearField, monthField, overtimeMonthLabel);
        updateUi(currentStampState, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel);
        List<ClockTime> clockTimes = currentStampState.getClockTimes();
        grid.setItems(clockTimes);
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

}
