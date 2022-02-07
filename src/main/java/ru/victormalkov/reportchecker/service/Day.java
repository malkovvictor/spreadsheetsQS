package ru.victormalkov.reportchecker.service;

import java.util.List;
import java.util.Objects;

public class Day {
    private String name;
    private int dayCache = 0;
    private int dayOnline = 0;
    private int dayTerminal = 0;
    private int nightCache = 0;
    private int nightOnline = 0;
    private int nightTerminal = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalizeDayName(name);
    }

    public int getDayOnline() {
        return dayOnline;
    }

    public int getDayCache() {
        return dayCache;
    }

    public int getDayTerminal() {
        return dayTerminal;
    }

    public int getNightOnline() {
        return nightOnline;
    }

    public int getNightCache() {
        return nightCache;
    }

    public int getNightTerminal() {
        return nightTerminal;
    }

    public void addDayOnline(int amount) {
        this.dayOnline += amount;
    }

    public void addDayCache(int amount) {
        this.dayCache += amount;
    }

    public void addDayTerminal(int amount) {
        this.dayTerminal += amount;
    }

    public void addNightOnline(int amount) {
        this.nightOnline += amount;
    }

    public void addNightCache(int amount) {
        this.nightCache += amount;
    }

    public void addNightTerminal(int amount) {
        this.nightTerminal += amount;
    }

    @Override
    public String toString() {
        return "DailyReport{" +
                "name=" + name +
                ", dayCache=" + dayCache +
                ", dayOnline=" + dayOnline +
                ", dayTerminal=" + dayTerminal +
                ", nightCache=" + nightCache +
                ", nightOnline=" + nightOnline +
                ", nightTerminal=" + nightTerminal +
                '}';
    }

    public String toPrettyString(Day comparedDay) {
        final boolean otherIsNull = comparedDay == null;
        if (otherIsNull) {
            comparedDay = this;
        }
        return name + (otherIsNull ? " (ВТОРОГО ОТЧЁТА НЕТ)" : "") +
                System.lineSeparator() +
                TableUtil.tableToString(List.of(
                                List.of("", "День", "Ночь"),
                                List.of("Наличные", compared(getDayCache(), comparedDay.getDayCache()), compared(getNightCache(), comparedDay.getNightCache())),
                                List.of("Перевод", compared(getDayOnline(), comparedDay.getDayOnline()), compared(getNightOnline(), comparedDay.getNightOnline())),
                                List.of("Терминал", compared(getDayTerminal(), comparedDay.getDayTerminal()), compared(getNightTerminal(), comparedDay.getNightTerminal()))
                        )
                ) +
                "------------------------------------------" +
                System.lineSeparator();
    }

    private String compared(int amount1, int amount2) {
        if (amount1 == amount2) {
            return Integer.toString(amount1);
        } else {
            return "! " + amount1 + "-" + amount2 + " = " + (amount1 - amount2);
        }
    }

    private String normalizeDayName(String dayName) {
        return dayName != null ? dayName.toLowerCase().replace(",", ".") : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return getDayCache() == day.getDayCache() && getDayOnline() == day.getDayOnline() && getDayTerminal() == day.getDayTerminal() && getNightCache() == day.getNightCache() && getNightOnline() == day.getNightOnline() && getNightTerminal() == day.getNightTerminal() && Objects.equals(getName(), day.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDayCache(), getDayOnline(), getDayTerminal(), getNightCache(), getNightOnline(), getNightTerminal());
    }
}
