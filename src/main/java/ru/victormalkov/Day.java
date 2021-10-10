package ru.victormalkov;

public class Day {
    int dayCache = 0;
    int dayOnline = 0;
    int dayTerminal = 0;
    int nightCache = 0;
    int nightOnline = 0;
    int nightTerminal = 0;

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
                "dayCache=" + dayCache +
                ", dayOnline=" + dayOnline +
                ", dayTerminal=" + dayTerminal +
                ", nightCache=" + nightCache +
                ", nightOnline=" + nightOnline +
                ", nightTerminal=" + nightTerminal +
                '}';
    }
}
