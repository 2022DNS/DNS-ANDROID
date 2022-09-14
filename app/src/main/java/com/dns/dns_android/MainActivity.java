package com.dns.dns_android;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dns.dns_lib.DnsPermission;
import com.dns.dns_lib.DnsRecognition;
import com.dns.dns_lib.DnsRecognitionListener;
import com.dns.dns_lib.DnsWaker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final int PERMISSION_REQUEST_CODE = 100;
    private DnsRecognition dnsRecognition;
    private DnsWaker dnsWaker;
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request permissions.
        if (DnsPermission.requestDnsPermissions(MainActivity.this, this, PERMISSION_REQUEST_CODE)) {
            showCameraPreview();
        }

        // Create DnsWaker object.
        dnsWaker = new DnsWaker(MainActivity.this);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    showCameraPreview();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void showCameraPreview() {
        dnsRecognition = new DnsRecognition(getApplicationContext(), DnsRecognition.BACK_CAMERA, new DnsRecognitionListener() {
            @Override
            public void detectFaceLandmarksNotCorrectlyListener() {

            }

            @Override
            public void failedToDetectFaceLandmarksListener() {

            }

            @Override
            public void failedToDetectFaceListener() {

            }

            @Override
            public void drowsyDrivingNotDetectedListener() {

            }

            @Override
            public void drowsyDrivingDetectedListener() {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}