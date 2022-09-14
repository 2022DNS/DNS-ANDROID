package com.dns.dns_lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * DnsMarker used to request warning area from DnsOpenApi server.
 *
 * @author Sohn Young Jin
 * @since 1.0.0
 */
public class DnsMarker {
    /**
     * Get nearby warning area list from DnsOpenApi server.
     *
     * @return List of warning area.
     */
    public static ArrayList<DnsMarkerObject> getNearbyWarningAreaList() {
        try {
            String response = (new DnsOpenApi().execute(DnsOpenApi.DNS_OPENAPI_SERVER + "/coordinate", DnsOpenApi.DEFAULT_CONNECTION_TIMEOUT, DnsOpenApi.DEFAULT_READ_TIMEOUT, null)).get();
            JSONArray jsonArray = new JSONArray(response);
            return DnsMarkerObject.fromJsonArray(jsonArray);
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get area data from DnsOpenApi server.
     *
     * @param latitude  Area latitude.
     * @param longitude Area longitude.
     * @return Warning area.
     */
    public static DnsMarkerObject getArea(double latitude, double longitude) {
        String strLatitude = String.valueOf(latitude).replace(".", "-");
        String strLongitude = String.valueOf(longitude).replace(".", "-");
        try {
            String response = (new DnsOpenApi().execute(DnsOpenApi.DNS_OPENAPI_SERVER + "/coordinate/lo/" + strLongitude + "/la/" + strLatitude, DnsOpenApi.DEFAULT_CONNECTION_TIMEOUT, DnsOpenApi.DEFAULT_READ_TIMEOUT, null)).get();
            JSONObject jsonObject = new JSONObject(response);
            return new DnsMarkerObject(latitude, longitude, jsonObject.getInt("count"));
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
