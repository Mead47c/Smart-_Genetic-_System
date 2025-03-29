package com.example.genessystem.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WikipediaConnector {

    public static String getDiseaseInfo(String diseaseName) {
        try {
            // Format the disease name for Wikipedia API
            String formattedName = diseaseName.replace(" ", "_").replaceAll("\\(.*?\\)", "").trim();
            String apiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + formattedName;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "JavaFX-Disease-Assistant");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "Wikipedia article not found for: " + diseaseName;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();

            // Extract summary from JSON response
            String json = response.toString();
            int start = json.indexOf("\"extract\":\"") + 10;
            int end = json.indexOf("\",\"", start);
            if (start != -1 && end != -1) {
                String summary = json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                return "\n\n" + summary;
            } else {
                return "No summary available for this disease.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching summary from Wikipedia.";
        }
    }
}
