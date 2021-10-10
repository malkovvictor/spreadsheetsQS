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

    public ArrayList<Day> getDays() {
        return days;
    }

    public void pushDay(Day day) {
        if (days == null) {
            days = new ArrayList<>();
        }
        days.add(day);
    }
}
