package nz.ac.vuw.swen301.assignment3.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Helper {
    public static final String HOST = "localhost";
    public static final int PORT = 8080;
    public static final String PATH = "/resthome4logs";
    public static final String LOGS_SERVICE = PATH + "/logs";
    public static final String STATS_SERVICE = PATH + "/stats";
    public static final String DOWNLOAD_FILE_NAME = "Logger Stats.xls";
    public static final String STATS_CONTENT_TYPE = "application/vnd.ms-excel";
    public static final String LOGS_CONTENT_TYPE = "application/json";
    public static final ArrayList<String> LEVELS = new ArrayList<String>(Arrays.asList("ALL", "OFF", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "TRACE"));

    // 200 search results matching criteria
    public static final int LOGS_GET__SEARCH_PARAM_CORRECT = HttpServletResponse.SC_OK;
    // 400 = bad input parameter
    public static final int LOGS_GET__SEARCH_PARAM_INCORRECT = HttpServletResponse.SC_BAD_REQUEST;
    // 201 items created
    public static final int LOGS_POST__ITEMS_CREATED = HttpServletResponse.SC_CREATED;
    //400 invalid input, object invalid
    public static final int LOGS_POST__INVALID_INPUT = HttpServletResponse.SC_BAD_REQUEST;
    //409 a log event with this id aleady exist
    public static final int LOGS_POST__ALREADY_EXISTS = HttpServletResponse.SC_CONFLICT;
    // 200 search results matching criteria
    public static final int STATS_GET__CORRECT = HttpServletResponse.SC_OK;
    // 200 search results matching criteria
    public static final int STATS_GET__INCORRECT = HttpServletResponse.SC_NOT_FOUND;

    private List<LogEvent> parseObjectArray(JSONArray array) {
        List<LogEvent> list = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            LogEvent log = new LogEvent(
                    (String) object.get("id"),
                    (String) object.get("message"),
                    (String) object.get("timestamp"),
                    (String) object.get("thread"),
                    (String) object.get("logger"),
                    (String) object.get("level"),
                    (String) object.get("errorDetails"));
            list.add(i, log);
        }

        return list;
    }

    public HttpResponse post(String json) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPort(PORT)
                .setPath(LOGS_SERVICE);
        URI uri = builder.build();

        // STRING TO STRING ENTITY
        StringEntity entity = null;

        // SET/ SEND CONTENT
        entity = new StringEntity(json);
        entity.setContentType("application/json");

        HttpPost request = new HttpPost(uri);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        return response;
    }

    public HttpResponse get(String level, int limit) throws URISyntaxException, IOException {
        HttpResponse response;
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPort(PORT)
                .setPath(LOGS_SERVICE)
                .setParameter("Level", level)
                .setParameter("Limit", String.valueOf(limit));
        URI uri = builder.build();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        response = httpClient.execute(request);
        
        return response;
    }

    public HttpResponse get(String level, String limit) throws URISyntaxException, IOException {
        HttpResponse response;
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPort(PORT)
                .setPath(LOGS_SERVICE)
                .setParameter("Level", level)
                .setParameter("Limit", limit);
        URI uri = builder.build();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        response = httpClient.execute(request);

        return response;
    }

    public List<LogEvent> toList(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        List<LogEvent> logsLIST = parseObjectArray((JSONArray) parser.parse(json));

        return logsLIST;
    }

    public String toJSON(List<LogEvent> list) {
        JSONArray array = new JSONArray();

        // LOGS TO JSON
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            LogEvent l = list.get(i);
            object.put("id", l.getId());
            object.put("timestamp", l.getTimestamp());
            object.put("thread", l.getThread());
            object.put("logger", l.getLogger());
            object.put("level", l.getLevel());
            object.put("message", l.getMessage());
            object.put("errorDetails", l.getErrorDetails());
            array.add(i, object);
        }
        return array.toJSONString();
    }

    public boolean isRunning() throws URISyntaxException, IOException {
        HttpResponse response;
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPort(PORT)
                .setPath(LOGS_SERVICE)
                .setParameter("Level", "WARN")
                .setParameter("Limit", "60");
        URI uri = builder.build();

        try{
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(uri);
            response = httpClient.execute(request);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return response.getStatusLine().getStatusCode() == LOGS_GET__SEARCH_PARAM_INCORRECT;
    }

    public HttpResponse getStats() throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPort(PORT)
                .setPath(STATS_SERVICE);
        URI uri = builder.build();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        return response;
    }

    public HSSFWorkbook toWorkbook(HttpResponse response) throws IOException {
        HSSFWorkbook w = null;

        response.setHeader("Content-Type", "application/vnd.ms-excel");

        try {
            w = (HSSFWorkbook) WorkbookFactory.create(new BufferedInputStream(response.getEntity().getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return w;
        }

        return w;
    }

    public void toFile(HSSFWorkbook w) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(DOWNLOAD_FILE_NAME));
            w.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

}
