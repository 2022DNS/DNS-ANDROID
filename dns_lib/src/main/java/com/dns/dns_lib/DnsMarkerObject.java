package com.dns.dns_lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * DnsMarkerObject used to save warning area date and mark into map api.
 *
 * @author Sohn Young Jin
 * @since 1.0.0
 */
public class DnsMarkerObject {
    /**
     * Marker latitude.
     */
    private double latitude;

    /**
     * Marker longitude.
     */
    private double longitude;

    /**
     * Area drowsy driving detected count.
     */
    private int count;

    /**
     * Default constructor.
     */
    public DnsMarkerObject() {
        this(0.0, 0.0, 0);
    }

    /**
     * Constructor with parameters.
     *
     * @param latitude  Marker latitude.
     * @param longitude Marker longitude.
     * @param count     Area drowsy driving detected count.
     */
    public DnsMarkerObject(double latitude, double longitude, int count) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.count = count;
    }

    /**
     * Get DnsMarkerObject list from json array.
     *
     * @param jsonArray Warning area json array.
     * @return DnsMarkerObject list.
     */
    public static ArrayList<DnsMarkerObject> fromJsonArray(JSONArray jsonArray) {
        ArrayList<DnsMarkerObject> dnsMarkerObjects = new ArrayList<>();

        for (int loop = 0; loop < jsonArray.length(); loop++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(loop);
                dnsMarkerObjects.add(new DnsMarkerObject(Double.valueOf(jsonObject.getString("latitude").replace("-", ".")), Double.valueOf(jsonObject.getString("longitude").replace("-", ".")), jsonObject.getInt("count")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dnsMarkerObjects;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
