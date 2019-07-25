package nz.ac.vuw.swen301.assignment3.server;

import org.apache.http.HttpResponse;
import org.apache.poi.hssf.usermodel.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class StatsServlet extends HttpServlet {
    private static ArrayList<LogEvent> serverLogs = new ArrayList<LogEvent>();
    private static String FILE_NAME = "Logger Stats";
    private static HSSFWorkbook workbook;
    private static Helper help = new Helper();

    public HSSFWorkbook create() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(FILE_NAME);
        Map<String, Object[]> data = new TreeMap<String, Object[]>();

        List<String> days = new ArrayList<>();
        for (int i = 0; i < serverLogs.size(); i++) {
            if (!days.contains(serverLogs.get(i).getDate()))
                days.add(serverLogs.get(i).getDate());
        }

        Object[] cols = new Object[days.size() + 1];
        cols[0] = " ";
        for (int i = 0; i < days.size(); i++)
            cols[i + 1] = days.get(i);
        data.put("0", cols);

        Map<String, LogRow> rows = new HashMap<>();

        for (LogEvent log : serverLogs) {
            LogRow logRow = new LogRow(log);

            if (rows.get(logRow.getName()) == null) {
                logRow.initDays(days);
                logRow.addDateCount(log.getDate());
                rows.put(logRow.getName(), logRow);
            } else {
                rows.get(logRow.getName()).addDateCount(log.getDate());
            }
        }
  
        int i = 1;
        for (String k : rows.keySet()) {
            data.put(i + "", rows.get(k).toOb());
            i++;
        }
  
        Set<String> keyset = data.keySet();
        int rownum = 0;

        for (String key : keyset) {
            HSSFRow row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                HSSFCell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer) obj);
            }
        }

        return workbook;
    }

    /**
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + FILE_NAME);

        try {
            serverLogs = new LogsServlet().getLogs();
            workbook = create();
            workbook.write(response.getOutputStream());
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            return;
        }
    }

    public class LogRow {
        String name;
        Map<String, Integer> dateCount;
        List<String> days;

        public LogRow(LogEvent log) {
            this.name = log.getLogger() + ", " + log.getLevel() + ", " + log.getThread();
            this.dateCount = new HashMap<String, Integer>();
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "LogRow{" +
                    "name='" + this.name + '\'' +
                    ", dateCount=" + this.dateCount +
                    '}';
        }

        public void initDays(List<String> days) {
            this.days = days;
            for (int i = 0; i < days.size(); i++)
                this.dateCount.put(days.get(i), 0);
        }

        public void addDateCount(String date) {
            this.dateCount.put(date, this.dateCount.get(date) + 1);
        }

        public Object[] toOb() {
            Object[] o = new Object[days.size() + 1];

            o[0] = this.name;
            for (int i = 0; i < this.days.size(); i++) // check +1
                o[i + 1] = this.dateCount.get(days.get(i));

            return o;
        }
    }
}