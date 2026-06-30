package de.julianweinelt.datacat;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Locale;

public class Reporter {
    private static final Logger log = LoggerFactory.getLogger(Reporter.class);

    public static void main(String[] args) throws Exception {
        FlatDarkLaf.setup();
        if (args.length == 0) return;
        if (args[0].startsWith("--code=")) {
            String path = args[1].substring("--path=".length());
            int exitCode = Integer.parseInt(args[0].substring("--code=".length()));
            ErrorReportAction action = showErrorDialog("Looks like DataCat crashed. Exit code: " + exitCode);

            String serverBase = (args.length > 2 && args[2].equalsIgnoreCase("--dev")) ? "http://localhost:7070/" :
                    "https://api.data-cat.de/";

            Path file1 = new File(path, "datacat.log").toPath();
            Path fileDebug = new File(path, "datacat-debug.log").toPath();

            switch (action) {
                case VIEW_LOGS -> {
                    try {
                        Runtime.getRuntime().exec("explorer.exe /select,\"" + path.replace("/", "\\") + "\"");
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                case SEND_LOGS -> {
                    if (sendRequest(URI.create(serverBase + "api/v1/errorreport"), file1, fileDebug, "{}"))
                        JOptionPane.showMessageDialog(null, "Thanks for sending us your log files." +
                                " This will help to improve DataCat and fix your problem as soon as possible. If you want, " +
                                "you can also go to our GitHub page to report this issue or write us an eMail to hello@data-cat.de",
                                "Logs have been sent", JOptionPane.INFORMATION_MESSAGE);
                }
                case SEND_LOGS_SYSTEM_INFO -> {
                    if (sendRequest(URI.create(serverBase + "api/v1/errorreport"), file1, fileDebug, getSystemInfo()))
                        JOptionPane.showMessageDialog(null, "Thanks for sending us your log files." +
                                        " This will help to improve DataCat and fix your problem as soon as possible. If you want, " +
                                        "you can also go to our GitHub page to report this issue or write us an eMail to hello@data-cat.de." +
                                        "\n\nSome important system information has been sent as well.",
                                "Logs have been sent", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    public static boolean sendRequest(
            URI uri,
            Path file1,
            Path file2,
            String json
    ) throws Exception {

        String boundary = "----JavaMultipartBoundary" + System.currentTimeMillis();

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(body, StandardCharsets.UTF_8);

        writer.write("--" + boundary + "\r\n");
        writer.write("Content-Disposition: form-data; name=\"data\"\r\n");
        writer.write("Content-Type: application/json\r\n\r\n");
        writer.write(json + "\r\n");

        writer.write("--" + boundary + "\r\n");
        writer.write("Content-Disposition: form-data; name=\"file1\"; filename=\"" + file1.getFileName() + "\"\r\n");
        writer.write("Content-Type: application/octet-stream\r\n\r\n");
        writer.flush();
        Files.copy(file1, body);
        body.write("\r\n".getBytes());

        writer.write("--" + boundary + "\r\n");
        writer.write("Content-Disposition: form-data; name=\"file2\"; filename=\"" + file2.getFileName() + "\"\r\n");
        writer.write("Content-Type: application/octet-stream\r\n\r\n");
        writer.flush();
        Files.copy(file2, body);
        body.write("\r\n".getBytes());

        writer.write("--" + boundary + "--\r\n");
        writer.flush();

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                JOptionPane.showMessageDialog(null, "Your report could not be sent. Please check " +
                        "your connection or report this issue at GitHub", "Failed to send data", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Your report could not be sent. Please check " +
                    "your connection or report this issue at GitHub. Error message: " + e.getMessage(),
                    "Failed to send data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private static String getSystemInfo() {
        JsonObject j = new JsonObject();
        String version = Reporter.class.getPackage().getImplementationVersion();
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String jvmName = System.getProperty("java.vm.name");
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        int cpuCores = Runtime.getRuntime().availableProcessors();
        String cpuArch = System.getProperty("os.arch");
        java.util.List<String> jvmArgs = ManagementFactory
                .getRuntimeMXBean()
                .getInputArguments();

        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean)
                        ManagementFactory.getOperatingSystemMXBean();

        long totalRam = osBean.getTotalMemorySize();
        long freeRam = osBean.getFreeMemorySize();
        String workingDir = System.getProperty("user.dir");
        Locale locale = Locale.getDefault();
        String language = locale.toString();
        String timezone = ZoneId.systemDefault().toString();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width;
        int height = screen.height;

        j.addProperty("version", version);
        JsonObject os = new JsonObject();
        os.addProperty("name", osName);
        os.addProperty("version", osVersion);
        os.addProperty("arch", osArch);
        j.add("os", os);
        JsonObject java = new JsonObject();
        java.addProperty("version", javaVersion);
        java.addProperty("vendor", javaVendor);
        j.add("java", java);
        JsonObject jvm = new JsonObject();
        jvm.addProperty("name", jvmName);
        jvm.add("args", new Gson().toJsonTree(jvmArgs));
        j.add("jvm", jvm);

        JsonObject system = new JsonObject();
        system.addProperty("maxMemory", maxMemory);
        system.addProperty("totalMemory", totalMemory);
        system.addProperty("freeMemory", freeMemory);
        system.addProperty("cpuCores", cpuCores);
        system.addProperty("cpuArch", cpuArch);
        system.addProperty("totalRam", totalRam);
        system.addProperty("freeRam", freeRam);
        system.addProperty("workingDir", workingDir);
        system.addProperty("language", language);
        system.addProperty("timezone", timezone);
        system.addProperty("width", width);
        system.addProperty("height", height);

        j.add("system", system);

        return j.toString();
    }


    private static ErrorReportAction showErrorDialog(String errorText) {
        JTextArea textArea = new JTextArea(errorText);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        Object[] options = {
                "Do not report",
                "Send logs",
                "Send logs & system info",
                "View Logs"
        };

        int result = JOptionPane.showOptionDialog(
                null,
                scrollPane,
                "Application Error",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[1]
        );

        return switch (result) {
            case 1 -> ErrorReportAction.SEND_LOGS;
            case 2 -> ErrorReportAction.SEND_LOGS_SYSTEM_INFO;
            case 3 -> ErrorReportAction.VIEW_LOGS;
            default -> ErrorReportAction.DO_NOT_REPORT;
        };
    }

    public enum ErrorReportAction {
        DO_NOT_REPORT,
        SEND_LOGS,
        SEND_LOGS_SYSTEM_INFO,
        VIEW_LOGS
    }
}
