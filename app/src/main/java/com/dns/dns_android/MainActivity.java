package com.dns.dns_android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dns.dns_lib.DnsMarker;
import com.dns.dns_lib.DnsMarkerObject;
import com.dns.dns_lib.DnsPermission;
import com.dns.dns_lib.DnsRecognition;
import com.dns.dns_lib.DnsRecognitionListener;
import com.dns.dns_lib.DnsWaker;
import com.dns.dns_lib.DnsWakerListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final int PERMISSION_REQUEST_CODE = 100;

    /**
     * DnsRecognition object.
     */
    private DnsRecognition dnsRecognition;

    /**
     * DnsWaker object.
     */
    private DnsWaker dnsWaker;

    /**
     * Google Map object.
     */
    private GoogleMap googleMap;

    /**
     * Constraint layout for display camera view.
     */
    private ConstraintLayout clCameraView;

    /**
     * Button for drowsy driving detection.
     */
    private AppCompatImageButton btnRecognition;

    /**
     * State value for recognition button.
     */
    private boolean recognitionEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clCameraView = findViewById(R.id.cl_cameraview);
        btnRecognition = findViewById(R.id.btn_recognition);

        findViewById(R.id.btn_loadwarningareas).setOnClickListener(view -> {
            new Thread(() -> {
                ArrayList<DnsMarkerObject> dnsMarkerObjectArrayList = DnsMarker.getNearbyWarningAreaList();
                runOnUiThread(() -> {
                    googleMap.clear();
                    for (DnsMarkerObject dnsMarkerObject : dnsMarkerObjectArrayList) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(dnsMarkerObject.getLatitude(), dnsMarkerObject.getLongitude()));
                        markerOptions.title("졸음횟수: " + dnsMarkerObject.getCount());
                        googleMap.addMarker(markerOptions);
                    }
                });
            }).start();
        });

        findViewById(R.id.btn_loadareadetail).setOnClickListener(view -> {
            new Thread(() -> {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                try {
                    Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (currentLocation == null) {
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                    DnsMarkerObject dnsMarkerObject = DnsMarker.getArea(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(dnsMarkerObject.getLatitude(), dnsMarkerObject.getLongitude()));
                    markerOptions.title("졸음횟수: " + dnsMarkerObject.getCount());
                    runOnUiThread(() -> {
                        googleMap.clear();
                        googleMap.addMarker(markerOptions);
                    });
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        btnRecognition.setOnClickListener(view -> {
            recognitionEnabled = !recognitionEnabled;
            if (recognitionEnabled) {
                dnsRecognition.startCapture();
                dnsRecognition.setRecognizing(true);
                btnRecognition.setImageResource(R.drawable.ic_videocam_black_24dp);
            } else {
                dnsRecognition.stopCapture();
                dnsRecognition.setRecognizing(false);
                btnRecognition.setImageResource(R.drawable.ic_videocam_off_black_24dp);
            }
        });

        // Check and request permissions.
        if (DnsPermission.requestDnsPermissions(MainActivity.this, this, PERMISSION_REQUEST_CODE)) {
            setupDns();
        }

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
                    setupDns();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setupDns() {
        // Create DnsRecognition object.
        dnsRecognition = new DnsRecognition(getApplicationContext(), DnsRecognition.FRONT_CAMERA, new DnsRecognitionListener() {
            @Override
            public void detectFaceLandmarksNotCorrectlyListener() {
                Log.d("Detection", "Detect face landmark not correctly");
                dnsRecognition.setRecognizing(true);
            }

            @Override
            public void failedToDetectFaceLandmarksListener() {
                Log.d("Detection", "Failed to detect face landmarks");
                dnsRecognition.setRecognizing(true);
            }

            @Override
            public void failedToDetectFaceListener() {
                Log.d("Detection", "Failed to detect face");
                dnsRecognition.setRecognizing(true);
            }

            @Override
            public void drowsyDrivingNotDetectedListener() {
                Log.d("Detection", "Drowsy driving not detected");
                dnsRecognition.setRecognizing(true);
            }

            @Override
            public void drowsyDrivingDetectedListener() {
                Log.d("Detection", "Drowsy driving detected");
                dnsWaker.runVoiceRecognizeWakerExample(MainActivity.this, true, R.raw.annoying_alarm, new DnsWakerListener() {
                    @Override
                    public void onDriverPassedTestSuccessfully() {

                    }

                    @Override
                    public void onDriverPassedTestFailed() {

                    }
                }, new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {

                    }

                    @Override
                    public void onDone(String s) {
                        dnsRecognition.setRecognizing(true);
                    }

                    @Override
                    public void onError(String s) {
                        dnsRecognition.setRecognizing(true);
                    }
                });
            }
        });
        dnsRecognition.addCameraView(clCameraView);

        // Create DnsWaker object.
        dnsWaker = new DnsWaker(MainActivity.this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}