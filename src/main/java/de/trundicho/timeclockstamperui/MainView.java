package de.trundicho.timeclockstamperui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import de.trundicho.timeclockstamperui.wsclient.ClockTimeResponse;
import de.trundicho.timeclockstamperui.wsclient.TimeClockStamperWsClient;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

    private RestTemplate restTemplate;

    @Autowired
    public MainView(TimeClockStamperWsClient timeClockStamperWsClient) {
        Button stampInOrOutButton = new Button("Stamp");
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

        Label overtimeMonthButton = new Label("Overtime month");
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
        Label overtimeMonthLabel = new Label();
        overtimePerMonthLayout.add(new Label("Overtime:"), overtimeMonthLabel);

        this.add(stampInOrOutHorizontalLayout);
        this.add(stampStateHorizontalLayout);
        this.add(workedTodayHorizontalLayout);
        this.add(overtimeCurrentMonthLayout);

        this.add(overtimePerMonthLayout);
        this.add(updateUiButton);

        //------------------ Initialize -----------------
        ClockTimeResponse currentStampState = timeClockStamperWsClient.getCurrentStampState();
        updateUi(currentStampState, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel);
        updateOvertimeMonth(timeClockStamperWsClient, yearField, monthField, overtimeMonthLabel);

        //------------------ LISTENERS -----------------
        stampInOrOutButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            ClockTimeResponse clockTimeResponse = timeClockStamperWsClient.stampInOrOut();
            updateOvertimeMonth(timeClockStamperWsClient, yearField, monthField, overtimeMonthLabel);
            updateUi(clockTimeResponse, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel);
        });

        updateUiButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            ClockTimeResponse clockTimeResponse = timeClockStamperWsClient.getCurrentStampState();
            updateOvertimeMonth(timeClockStamperWsClient, yearField, monthField, overtimeMonthLabel);
            updateUi(clockTimeResponse, workedToadyLabel, stampStateLabel, overtimeCurrentMonthLabel);
        });

    }

    private void updateOvertimeMonth(TimeClockStamperWsClient timeClockStamperWsClient, IntegerField yearField, IntegerField monthField,
            Label overtimeMonthLabel) {
        Integer year = yearField.getValue();
        Integer month = monthField.getValue();
        if (year!= null && month !=null)  {
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
