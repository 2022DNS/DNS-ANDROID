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

/**
 * DnsPermission class is for easily obtaining camera permission. If the permission is denied, it moves to the application permission setting screen through its own dialog.
 */
public class DnsPermission {

    /**
     * Request camera permission.
     *
     * @param context Context.
     * @param activity Activity.
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
}