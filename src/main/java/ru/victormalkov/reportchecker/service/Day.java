package ru.victormalkov.reportchecker.service;

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
        this.name = name;
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

    public String toPrettyString() {

        return name +
                System.lineSeparator() +
                "\t\t\tДЕНЬ\tНОЧЬ" +
                System.lineSeparator() +
                "Наличные\t" +
                getDayCache() +
                "\t\t" +
                getNightCache() +
                System.lineSeparator() +
                "Перевод\t\t" +
                getDayOnline() +
                "\t\t" +
                getNightOnline() +
                System.lineSeparator() +
                "Терминал\t" +
                getDayTerminal() +
                "\t\t" +
                getNightTerminal() +
                System.lineSeparator() +
                "------------------------------------------" +
                System.lineSeparator();
    }
}
