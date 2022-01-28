package ru.victormalkov.reportchecker.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DailyReport {
    private static final Logger logger = LogManager.getLogger(AuthUtil.APP_NAME);

    private final Map<String, Day> daysMap = new LinkedHashMap<>();

    public Day getDay(String dayName) {
        return daysMap.get(dayName);
    }

    public Collection<Day> getDays() {
        return daysMap.values();
    }

    public void pushDay(Day day) {
        logger.debug("pushing day " + day.toString());
        daysMap.put(day.getName(), day);
    }

    public void debugPrintKeys() {
        logger.debug(daysMap.keySet());
    }
}
