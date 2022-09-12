package com.dns.dns_lib;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * DnsPermission class is for easily obtaining camera permission. If the permission is denied, it moves to the application permission setting screen through its own dialog.
 *
 * @author Sohn Young Jin
 * @since 1.0.0
 */
public class DnsPermission {

    /**
     * Request camera permission.
     *
     * @param context     Context.
     * @param activity    Activity.
     * @param requestCode Request code for onRequestPermissionResult().
     * @return Return true when camera permission already granted.
     */
    public static boolean requestCameraPermission(Context context, Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Camera permission already granted.
            return true;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            // Camera permission denied and need to show request permission rationale.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setTitle("Camera Permission");
            alertDialogBuilder.setMessage("You need camera permission to use this service.");
            alertDialogBuilder.setPositiveButton("Setting", (dialogInterface, i) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            });
            alertDialogBuilder.show();
        } else {
            // Camera permission is not granted.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
        }

        return false;
    }

    /**
     * Request audio permission.
     *
     * @param context     Context.
     * @param activity    Activity.
     * @param requestCode Request code for onRequestPermissionResult().
     * @return Return true when audio permission already granted.
     */
    public static boolean requestAudioPermission(Context context, Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Audio permission already granted.
            return true;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            // Audio permission denied and need to show request permission rationale.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setTitle("Audio Permission");
            alertDialogBuilder.setMessage("You need audio permission to use this service.");
            alertDialogBuilder.setPositiveButton("Setting", (dialogInterface, i) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            });
            alertDialogBuilder.show();
        } else {
            // Audio permission is not granted.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
        }

        return false;
    }

    /**
     * Request gps permission.
     *
     * @param context     Context.
     * @param activity    Activity.
     * @param requestCode Request code for onRequestPermissionResult().
     * @return Return true when gps permission already granted.
     */
    public static boolean requestGpsPermission(Context context, Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Gps permission already granted.
            return true;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Gps permission denied and need to show request permission rationale.
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setTitle("GPS Permission");
            alertDialogBuilder.setMessage("You need gps permission to use this service.");
            alertDialogBuilder.setPositiveButton("Setting", (dialogInterface, i) -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            });
            alertDialogBuilder.show();
        } else {
            // Gps permission is not granted.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
        }

        return false;
    }

    /**
     * Request permissions.
     *
     * @param context     Context.
     * @param activity    Activity.
     * @param requestCode Request code for onRequestPermissionResult().
     * @param permissions Request permissions.
     * @return Return true when permissions already granted.
     */
    public static boolean requestPermissions(Context context, Activity activity, int requestCode, ArrayList<String> permissions) {
        List<String> notGrantedPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission);
            }
        }

        if (notGrantedPermissions.size() == 0) {
            return true;
        }

        ActivityCompat.requestPermissions(activity, notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]), requestCode);

        return false;
    }
}