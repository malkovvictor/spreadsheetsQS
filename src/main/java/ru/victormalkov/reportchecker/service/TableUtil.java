package ru.victormalkov.reportchecker.service;

import java.util.List;

public class TableUtil {
    public static final int SPACE_BETWEEN_COLUMNS = 3;

    public static String tableToString(List<List<String>> table) {
        int[] columnsLength = new int[table.get(0).size()];
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String s : table.get(0)) {
            columnsLength[i++] = s.length();
        }
        for (List<String> row : table) {
            i = 0;
            for (String cell : row) {
                if (cell.length() > columnsLength[i]) {
                    columnsLength[i] = cell.length();
                }
                i++;
            }
        }

        for (List<String> row : table) {
            i = 0;
            for (String cell : row) {
                result.append(cell);
                result.append(" ".repeat(SPACE_BETWEEN_COLUMNS + Math.max(0, columnsLength[i] - cell.length())));
                i++;
            }
            result.append(System.lineSeparator());
        }

        return result.toString();
    }
}
