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
        StringBuilder s = new StringBuilder();
        s.append(name);
        s.append(System.lineSeparator());
        s.append("\t\t\tДЕНЬ\tНОЧЬ");
        s.append(System.lineSeparator());
        s.append("Наличные\t");
        s.append(getDayCache());
        s.append("\t\t");
        s.append(getNightCache());
        s.append(System.lineSeparator());
        s.append("Перевод\t\t");
        s.append(getDayOnline());
        s.append("\t\t");
        s.append(getNightOnline());
        s.append(System.lineSeparator());
        s.append("Терминал\t");
        s.append(getDayTerminal());
        s.append("\t\t");
        s.append(getNightTerminal());
        s.append(System.lineSeparator());
        s.append("------------------------------------------");
        s.append(System.lineSeparator());

        return s.toString();
    }
}
