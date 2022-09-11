package com.dns.dns_lib;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * DnsOpenApi class helps to easily implement communication with OpenAPI server provided by DNS.
 *
 * @author Sohn Young Jin
 * @since 1.0.0
 */
public class DnsOpenApi extends AsyncTask<String, String, String> {
    public static int REQ_DROWSY_DRIVING_DETECTION = 1;
    public static int REQ_LIST_OF_DROWSY_DRIVING_AREA = 2;
    public static int RES_DROWSY_DRIVING_DETECTION = 1001;
    public static int RES_LIST_OF_DROWSY_DRIVING_AREA = 1002;

    /**
     * Http default connection timeout.
     */
    public static String DEFAULT_CONNECTION_TIMEOUT = "2000";

    /**
     * Http default read timeout.
     */
    public static String DEFAULT_READ_TIMEOUT= "2000";

    /**
     * Send request to url in parameter. If failed, return null.<br>
     * <p>
     * Parameters<br>
     * string[0]: URL<br>
     * string[1]: connect timeout<br>
     * string[2]: read timeout<br>
     * string[3]: send data
     *
     * @param strings Data used in connection.
     * @return String Connection result.
     */
    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setConnectTimeout(Integer.valueOf(strings[1]));
            httpURLConnection.setReadTimeout(Integer.valueOf(strings[2]));
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(strings[3]);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            httpURLConnection.disconnect();

            String result = stringBuffer.toString();
            result = result.substring(1, result.length() - 1).replace("\\", "");

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}