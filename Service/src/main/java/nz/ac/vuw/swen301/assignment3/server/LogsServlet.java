package nz.ac.vuw.swen301.assignment3.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class LogsServlet extends HttpServlet {
  private static final int MINLOGS = 0;
  private static final int MAXLOGS = 50;
  private static final ArrayList<String> LEVELS = new ArrayList<String>(Arrays.asList("ALL", "OFF", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "TRACE"));
  private static final String EMPTYJSON = "[]";
  public static ArrayList<LogEvent> serverLogs = new ArrayList<LogEvent>();
  private static Helper help = new Helper();

  public ArrayList<LogEvent> getLogs() {
    return serverLogs;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<LogEvent> filteredJsonLogList = new ArrayList<LogEvent>();
    String json;
    String level;
    String limit;

    level = request.getParameter("Level");
    limit = request.getParameter("Limit");
    response.setContentType("application/json");

    Collections.sort(serverLogs);

    try {
      if (level == null || limit == null)
        throw new Exception("Params not given");

      if (!(Integer.parseInt(limit) >= MINLOGS && Integer.parseInt(limit) <= MAXLOGS))
        throw new Exception("Limit not within bounds");

      if (!LEVELS.contains(level))
        throw new Exception("Level not recognised");

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

      PrintWriter out = response.getWriter();
      out.print(EMPTYJSON);
      out.close();
      return;
    }

    response.setStatus(HttpServletResponse.SC_OK);

    if (!serverLogs.isEmpty()) {
      Collections.sort(serverLogs);

      if (!level.equals("ALL")) {
        String finalLevel = level;
        filteredJsonLogList = serverLogs.stream()
            .filter(log -> log.sameOrHigherLevel(finalLevel))
            .limit(Integer.parseInt(limit))
            .collect(Collectors.toList());
      } else {
        filteredJsonLogList = serverLogs.stream()
            .limit(Integer.parseInt(limit))
            .collect(Collectors.toList());
      }
    }

    json = help.toJSON(filteredJsonLogList);

    PrintWriter out = response.getWriter();
    out.print(json);
    out.close();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String logsJSON;
    List<LogEvent> logsLIST;
    List<LogEvent> duplicates = new ArrayList<>();

    response.setContentType("application/json");

    try {
      logsJSON = request.getReader().lines().collect(Collectors.joining());
      logsLIST = help.toList(logsJSON);
      for(LogEvent l : logsLIST)

        if (logsLIST.isEmpty()) throw new Exception("Logs are Empty");

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    for (int i = 0; i < logsLIST.size(); i++) {         
      if (serverLogs.contains(logsLIST.get(i)))       
        duplicates.add(logsLIST.get(i));
      else                                            
        serverLogs.add(logsLIST.get(i));
    }

    if (duplicates.size() > 0) 
      response.setStatus(HttpServletResponse.SC_CONFLICT);
    else 
      response.setStatus(HttpServletResponse.SC_CREATED);

  }


}




