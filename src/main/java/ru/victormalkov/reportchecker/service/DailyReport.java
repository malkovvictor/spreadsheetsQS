package ru.victormalkov.reportchecker.service;

import java.util.ArrayList;

public class DailyReport {
    private ArrayList<Day> days;

    public Day getDay(int number) {
        if (number <= days.size()) {
            return days.get(number - 1);
        } else {
            return null;
        }
    }

    public void pushDay(Day day) {
        days.add(day);
    }
}
