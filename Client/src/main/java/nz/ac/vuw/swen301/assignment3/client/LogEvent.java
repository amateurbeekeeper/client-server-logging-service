package nz.ac.vuw.swen301.assignment3.client;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class LogEvent implements Comparable<LogEvent> {
    private SimpleDateFormat TIMESTAMP_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("mm:ss.SSS'Z'");
    private String id;
    private String timestamp;
    private String logger;
    private String thread;
    private String message;
    private String level;
    private String errorDetails;
    private Date date;

    /**
     * @param id
     * @param message
     * @param timestamp
     * @param thread
     * @param logger
     * @param level
     * @param errorDetails
     */
    public LogEvent(String id,
                    String message,
                    String timestamp,
                    String thread,
                    String logger,
                    String level,
                    String errorDetails) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
      
        try {
            this.date = TIMESTAMP_FORMAT.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
           
        }
      
        this.thread = thread;
        this.logger = logger;
        this.level = level;
        this.errorDetails = errorDetails;
    }

    /**
     * FROM LOGGING EVENT
     *
     * @param e
     */
    public LogEvent(LoggingEvent e) {
        this.date = new Date(e.timeStamp);
        this.timestamp = TIMESTAMP_FORMAT.format(new Date(e.timeStamp));
        this.id = UUID.randomUUID().toString();
        this.message = e.getMessage().toString();
        this.thread = e.getThreadName();
        this.logger = e.getLoggerName();
        this.level = e.getLevel().toString();
        this.errorDetails = " ";
    }

    /**
     * EMPTY, TO POPULATE WHEN MAKING JSON
     */
    public LogEvent() {

    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogEvent)) return false;
        LogEvent logEvent = (LogEvent) o;
        return getId().equals(logEvent.getId());
    }

    
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMessage(), getTimestamp(), getThread(), getLogger(), getLevel(), getErrorDetails());
    }

    public String getDate() {
        return DATE_FORMAT.format(this.date);
    }

    public String getTime() {
        return TIME_FORMAT.format(this.date);
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return this.logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getDateCompare() {
        return this.date;
    }

    @Override
    public int compareTo(LogEvent o) {
        try {
            return o.getDateCompare().compareTo(this.date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean sameOrHigherLevel(String l) {
        return Level.toLevel(l).isGreaterOrEqual(Level.toLevel(this.level));
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", thread='" + thread + '\'' +
                ", logger='" + logger + '\'' +
                ", level='" + level + '\'' +
                ", errorDetails='" + errorDetails + '\'' +
                '}';
    }

}
