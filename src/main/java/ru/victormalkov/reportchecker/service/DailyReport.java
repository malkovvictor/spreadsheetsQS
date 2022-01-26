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
        return daysMap.get(normalizeDayName(dayName));
    }

    public Collection<Day> getDays() {
        return daysMap.values();
    }

    public void pushDay(Day day) {
        logger.info("pushing day " + day.toString());
        daysMap.put(normalizeDayName(day.getName()), day);
    }

    private String normalizeDayName(String dayName) {
        String result = dayName != null ? dayName.toLowerCase().replace(",", ".") : null;
        logger.info(String.format("Normalizing string %s, result is %s", dayName, result));
        return result;
    }

    public void debugPrintKeys() {
        logger.info(daysMap.keySet());
    }
}
