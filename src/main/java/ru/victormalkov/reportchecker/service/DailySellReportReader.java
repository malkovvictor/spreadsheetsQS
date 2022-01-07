package ru.victormalkov.reportchecker.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailySellReportReader {
    private static final Logger logger = LogManager.getLogger(AuthUtil.APP_NAME);

    private static final String regexprCache = "(\\d+)(?![ТTЮтю0-9])";
    private static final String regexprOnline = "(\\d+)(?=[Юю])";
    private static final String regexprTerminal = "(\\d+)(?=[ТTт])";
    private static final Pattern pcache = Pattern.compile(regexprCache);
    private static final Pattern ponline = Pattern.compile(regexprOnline);
    private static final Pattern pterminal = Pattern.compile(regexprTerminal);

    public DailyReport readReport(String spreadsheetId) throws GeneralSecurityException, IOException {
        DailyReport result = new DailyReport();
        Sheets sheetsService = AuthUtil.getSheetsService();

        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
                .setIncludeGridData(true)
                .execute();

        List<Sheet> sheets = spreadsheet.getSheets();
        for (Sheet sheet : sheets) {
            logger.info("Sheet: " + sheet.getProperties().getTitle());
            logger.info("id: " + sheet.getProperties().getSheetId());
            GridData data = sheet.getData().get(0);
            if (data == null) {
                logger.warn("No data sound");
            } else {
                List<RowData> rdata = data.getRowData();
                if (rdata == null || rdata.size() < 2) {
                    logger.warn("Too few rows");
                } else {
                    List<Integer> borders = findBorders(rdata);
                    Day report = new Day();
                    report.setName(sheet.getProperties().getTitle());

                    if (borders.size() != 3) {
                        logger.warn("not all borders found -- cannot parse this sheet");
                    } else {
                        doCount(rdata, 2, borders.get(0), report, 2);
                        doCount(rdata, borders.get(0), borders.get(1), report, 1);
                        doCount(rdata, borders.get(1) + 2, borders.get(2), report, 1);
                        doCount(rdata, borders.get(2) + 2, rdata.size(), report, 1);
                        result.pushDay(report);
                    }
                }
            }
        }
        return result;
    }

    private List<Integer> findBorders(List<RowData> rdata) {
        List<Integer> borders = new ArrayList<>();
        for (int i = 2; i < rdata.size(); i++) {
            RowData row = rdata.get(i);
            List<CellData> rowdata = row.getValues();
            if (rowdata == null || rowdata.size() < 2) {
                // nothing
            } else if (borders.size() < 1) {
                CellData cdata = rowdata.get(0);
                if (cdata.getFormattedValue() != null &&
                        cdata.getFormattedValue().trim().equalsIgnoreCase("кальян")) {
                    borders.add(i);
                }
            } else if (borders.size() < 2) {
                CellData cdata = rowdata.get(0);
                if (cdata.getFormattedValue() != null &&
                        cdata.getFormattedValue().trim().equalsIgnoreCase("TRON BLACK\\TRON WHITE")) {
                    borders.add(i);
                }
            } else {
                CellData cdata = rowdata.get(0);
                if (cdata.getFormattedValue() != null &&
                        cdata.getFormattedValue().trim().equalsIgnoreCase("VIP\\Bootcamp")) {
                    borders.add(i);
                    break;
                }
            }
        }
        logger.info("borders found: " +  borders.toString());
        return borders;
    }

    public static final double eps = 0.02;
    private boolean isGreen(Color color) {
        return n0(color.getGreen()) > n0(color.getBlue()) + eps &&
                n0(color.getGreen()) > n0(color.getRed()) + eps;
    }

    private boolean isGreen(CellData data) {
        if (data == null || data.getEffectiveFormat() == null) return false;
        return isGreen(data.getEffectiveFormat().getBackgroundColor());
    }

    /* RGB(0, 1, 1) is blue */
    private boolean isBlue(Color color) {
        return n0(color.getBlue()) >= n0(color.getGreen()) &&
                n0(color.getBlue()) > n0(color.getRed()) + eps;
    }
    private boolean isBlue(CellData data) {
        if (data == null || data.getEffectiveFormat() == null) return false;
        return isBlue(data.getEffectiveFormat().getBackgroundColor());
    }


    private boolean isRed(Color color) {
        return n0(color.getRed()) > n0(color.getGreen()) + eps &&
                n0(color.getRed()) > n0(color.getBlue()) + eps;
    }
    private boolean isRed(CellData data) {
        if (data == null || data.getEffectiveFormat() == null) return false;
        return isRed(data.getEffectiveFormat().getBackgroundColor());
    }


    private static Float n0(Float f) {
        return f == null ? 0 : f;
    }

    private void doCount(List<RowData> rdata, int from, int to, Day report, int fromColumn) {
        for (int i = from; i < to; i++) {
            List<CellData> rowdata = rdata.get(i).getValues();
            if (rowdata != null) {
                for (int cell = fromColumn; cell < rowdata.size(); cell++) {
                    CellData cdata = rowdata.get(cell);
                    if (isBlue(cdata) || isGreen(cdata)) {
                        String s = cdata.getFormattedValue();
                        if (s == null || s.isEmpty()) {
                            continue;
                        }
                        Matcher m = pcache.matcher(s);
                        while (m.find()) {
                            if (isBlue(cdata)) {
                                report.addDayCache(Integer.parseInt(m.group()));
                            } else {
                                report.addNightCache(Integer.parseInt(m.group()));
                            }
                        }

                        m = ponline.matcher(s);
                        while (m.find()) {
                            if (isBlue(cdata)) {
                                report.addDayOnline(Integer.parseInt(m.group()));
                            } else {
                                report.addNightOnline(Integer.parseInt(m.group()));
                            }
                        }

                        m = pterminal.matcher(s);
                        while (m.find()) {
                            if (isBlue(cdata)) {
                                report.addDayTerminal(Integer.parseInt(m.group()));
                            } else {
                                report.addNightTerminal(Integer.parseInt(m.group()));
                            }
                        }
                    }
                }
            }
        }

    }


}
