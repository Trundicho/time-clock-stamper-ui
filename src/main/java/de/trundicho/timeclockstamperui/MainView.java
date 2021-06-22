package de.trundicho.timeclockstamperui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

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

        this.add(stampInOrOutHorizontalLayout);
        this.add(stampStateHorizontalLayout);
        this.add(workedTodayHorizontalLayout);
        this.add(overtimeMonthLayout);

        //------------------ Initialize -----------------
        updateUi( timeClockStamperWsClient, workedToadyLabel, stampStateLabel, overtimeMonthLabel);

        //------------------ LISTENERS -----------------
        stampInOrOutButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            System.out.println(timeClockStamperWsClient.stampInOrOut());
            updateUi( timeClockStamperWsClient, workedToadyLabel, stampStateLabel, overtimeMonthLabel);
        });
    }

    private void updateUi( TimeClockStamperWsClient timeClockStamperWsClient, Label workedToadyLabel, Label stampStateLabel,
            Label overtimeMonthLabel) {
        String hoursWorkedToday = timeClockStamperWsClient.getHoursWorkedToday();
        workedToadyLabel.setText(hoursWorkedToday);
        String currentStampState = timeClockStamperWsClient.getCurrentStampState();
        stampStateLabel.setText(currentStampState);
        String overtimeMonth = timeClockStamperWsClient.getOvertimeMonth();
        overtimeMonthLabel.setText(overtimeMonth);
    }

}
