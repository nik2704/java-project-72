package hexlet.code.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    public static String getUrlFormatted(String urlFormData) {
        if (urlFormData == null || urlFormData.isEmpty()) {
            LOGGER.log(Level.INFO, "Attempt to insert an empty URL");
            return "";
        }

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlFormData);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();

            result.append(protocol);
            result.append("://");
            result.append(host);
            result.append(port > 0 ? ":" + port : "");

            return result.toString();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Attempt to insert an incorrect URL: " + urlFormData);
        }

        return "";
    }

    public static Document getBody(String body) {
        return Jsoup.parse(body);
    }
}
