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
        HorizontalLayout overtimeMonthLayout = new HorizontalLayout();
        Label overtimeMonthLabel = new Label();
        overtimeMonthLayout.add(overtimeMonthButton, overtimeMonthLabel);

        Button updateUiButton = new Button("Update ui");

        this.add(stampInOrOutHorizontalLayout);
        this.add(stampStateHorizontalLayout);
        this.add(workedTodayHorizontalLayout);
        this.add(overtimeMonthLayout);
        this.add(updateUiButton);

        //------------------ Initialize -----------------
        ClockTimeResponse currentStampState = timeClockStamperWsClient.getCurrentStampState();
        updateUi(currentStampState, workedToadyLabel, stampStateLabel, overtimeMonthLabel);

        //------------------ LISTENERS -----------------
        stampInOrOutButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            ClockTimeResponse clockTimeResponse = timeClockStamperWsClient.stampInOrOut();
            updateUi(clockTimeResponse, workedToadyLabel, stampStateLabel, overtimeMonthLabel);
        });

        updateUiButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            ClockTimeResponse clockTimeResponse = timeClockStamperWsClient.getCurrentStampState();
            updateUi(clockTimeResponse, workedToadyLabel, stampStateLabel, overtimeMonthLabel);
        });

    }

    private void updateUi(ClockTimeResponse clockTimeResponse, Label workedToadyLabel, Label stampStateLabel,
            Label overtimeMonthLabel) {
        workedToadyLabel.setText(clockTimeResponse.getHoursWorkedToday());
        stampStateLabel.setText(clockTimeResponse.getCurrentState().name());
        String overtimeMonth = clockTimeResponse.getOvertimeMonth();
        overtimeMonthLabel.setText(overtimeMonth);
    }

}
