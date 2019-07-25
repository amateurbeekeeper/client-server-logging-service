package nz.ac.vuw.swen301.assignment3.client;

import org.apache.http.HttpResponse;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * uses the logs/ post service developed in the server
 * part to send log events to the server.
 */
public class Resthome4LogsAppender extends AppenderSkeleton {
    private static Helper help = new Helper();
    private final long MAX = 1;
    private List<LogEvent> logs = new ArrayList<LogEvent>();
    private List<LogEvent> discardedLogs = new ArrayList<LogEvent>();

    public Resthome4LogsAppender() {
        Properties log4jProp = new Properties();
        log4jProp.setProperty("log4j.rootLogger", "ALL");
        PropertyConfigurator.configure(log4jProp);
    }

    public List<LogEvent> getDiscardedLogs(){
        return discardedLogs;
    }

    @Override
    public void append(LoggingEvent event) {
        LogEvent e = new LogEvent(event);

        // ADD LOGS TO LOGS LIST
        logs.add(e);

        // IF MAX SIZE REACHED THEN DOPOST TO SERVLET
        if (logs.size() >= MAX) {
            HttpResponse r = null;

            // CREATE JSON
            String json = help.toJSON(logs);

            try {
                // POST LOGS
                r = help.post(json);

                // HANDLE REPONSE
                if (r.getStatusLine().getStatusCode() == help.LOGS_POST__ITEMS_CREATED
                        || r.getStatusLine().getStatusCode() == help.LOGS_POST__ALREADY_EXISTS) {
                    discardedLogs.addAll(logs);
                    logs.clear();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
