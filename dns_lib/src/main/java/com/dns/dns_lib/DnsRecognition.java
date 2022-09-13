package com.dns.dns_lib;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * DnsRecognition class is a class in charge of drowsiness recognition using OpenCV.
 *
 * @author Sohn Young Jin
 * @since 1.0.0
 */
public class DnsRecognition {
    /**
     * Back camera.
     */
    public final static int BACK_CAMERA = 0;

    /**
     * Front wide camera.
     */
    public final static int FRONT_WIDE_CAMERA = 1;

    /**
     * Front camera.
     */
    public final static int FRONT_CAMERA = 2;

    /**
     * Detection results.
     */
    public final static int DETECT_FACE_LANDMARKS_NOT_CORRECTLY = -3;
    public final static int FAILED_TO_DETECT_FACE_LANDMARKS = -2;
    public final static int FAILED_TO_DETECT_FACE = -1;
    public final static int DROWSY_DRIVING_NOT_DETECTED = 0;
    public final static int DROWSY_DRIVING_DETECTED = 1;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Load OpenCV failed.
            Log.e("Load OpenCV", "Failed to load OpenCV");
        } else {
            // Load OpenCV success.
            Log.d("Load OpenCV", "OpenCV load successfully");
        }
    }

    /**
     * Selected camera type.
     */
    private final int cameraType;
    /**
     * Camera view listener.
     */
    private final CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener;
    /**
     * Original image received from the camera.
     */
    private Mat originalFrame;
    /**
     * Modified image with rotation and flip effects applied to make it look right on Android.
     */
    private Mat modifedFrame;
    /**
     * Camera view.
     */
    private final JavaCameraView cameraView;
    /**
     * Collected camera frames.
     */
    private final ArrayList<Mat> frames;
    /**
     * Number of frames displayed in 1 second.
     */
    private int frameCount = 0;
    /**
     * Last frame check time.
     */
    private double lastFrameTime;
    /**
     * Control frame converting.
     */
    private boolean stopConverting = false;
    /**
     * Dns recognition result listener.
     */
    private final DnsRecognitionListener dnsRecognitionListener;
    /**
     * Location manager for get latitude and longitude.
     */
    private final LocationManager locationManager;
    /**
     * SimpleDateFormat used for convert time to declared format.
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * DnsRecognition constructor.
     *
     * @param context                Application context.
     * @param cameraType             Camera type(Back: 0, Front_Wide: 1, Front: 2)
     * @param dnsRecognitionListener Listener for recognition result.
     */
    public DnsRecognition(Context context, int cameraType, DnsRecognitionListener dnsRecognitionListener) {
        frames = new ArrayList<>();
        this.dnsRecognitionListener = dnsRecognitionListener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Set camera view.
        this.cameraType = cameraType;
        cameraView = new JavaCameraView(context, cameraType);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        cameraView.setLayoutParams(layoutParams);
        cameraView.disableView();
        cameraViewListener = new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                originalFrame = new Mat(height, width, CvType.CV_8UC4);
                lastFrameTime = System.currentTimeMillis();
            }

            @Override
            public void onCameraViewStopped() {
                originalFrame.release();
                modifedFrame.release();
            }

            /**
             * You can control/manage camera frame in this onCameraFrame method.
             */
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                // Release before frame to prevent from memory leak.
                if (originalFrame != null) {
                    originalFrame.release();
                }
                if (modifedFrame != null) {
                    modifedFrame.release();
                }

                // Change camera frame to gray scale.
                originalFrame = inputFrame.gray();

                // Copy original frame info to rotate frame in android.
                modifedFrame = originalFrame.t();

                // Check camera type and modify frame.
                if (cameraType == 0) {
                    // If selected camera type is back camera.
                    Core.flip(modifedFrame, modifedFrame, 1);
                } else if (cameraType == 1 || cameraType == 2) {
                    // If selected camera type is front camera.
                    Core.flip(modifedFrame, modifedFrame, -1);
                }
                Imgproc.resize(modifedFrame, modifedFrame, originalFrame.size());

                // Check camera frame.
                frameCount++;
                double currentTime = System.currentTimeMillis();
                if (currentTime - lastFrameTime > 1000) {
                    System.out.println("Current frame: " + frameCount);
                    frameCount = 0;
                    lastFrameTime = currentTime;
                }

                if (stopConverting == false) {
                    if (frames.size() == 10) {
                        stopConverting = true;
                        // If more than 10 frames collected, send images to DNS OpenAPI Server.
                        new Thread(() -> {
                            // Change mat to byte array and encode into base64.
                            ArrayList<String> encodedStrings = new ArrayList<>();
                            for (int loop = 0; loop < frames.size(); loop++) {
                                MatOfByte matOfByte = new MatOfByte();
                                Imgcodecs.imencode(".jpg", frames.get(loop), matOfByte);
                                encodedStrings.add(Base64.encodeToString(matOfByte.toArray(), Base64.NO_WRAP));
                            }

                            // Create open api send data.
                            JSONObject sendData = new JSONObject();
                            try {
                                sendData.put("request_code", DnsOpenApi.REQ_DROWSY_DRIVING_DETECTION);

                                JSONArray images = new JSONArray();
                                for (int loop = 0; loop < frames.size(); loop++) {
                                    images.put(encodedStrings.get(loop));
                                }
                                sendData.put("images", images);

                                sendData.put("time", simpleDateFormat.format(new Date(System.currentTimeMillis())));

                                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                JSONObject areaData = new JSONObject();
                                areaData.put("la", currentLocation.getLatitude());
                                areaData.put("lo", currentLocation.getLongitude());
                                sendData.put("area", areaData);

                                String strResponse = (new DnsOpenApi().execute("", DnsOpenApi.DEFAULT_CONNECTION_TIMEOUT, DnsOpenApi.DEFAULT_READ_TIMEOUT, sendData.toString())).get();
                                JSONObject response = new JSONObject(strResponse);

                                if (response.getInt("response_code") == DnsOpenApi.RES_DROWSY_DRIVING_DETECTION) {
                                    switch (response.getInt("result")) {
                                        case DETECT_FACE_LANDMARKS_NOT_CORRECTLY:
                                            dnsRecognitionListener.detectFaceLandmarksNotCorrectlyListener();
                                            break;
                                        case FAILED_TO_DETECT_FACE_LANDMARKS:
                                            dnsRecognitionListener.failedToDetectFaceLandmarksListener();
                                            break;
                                        case FAILED_TO_DETECT_FACE:
                                            dnsRecognitionListener.failedToDetectFaceListener();
                                            break;
                                        case DROWSY_DRIVING_NOT_DETECTED:
                                            dnsRecognitionListener.drowsyDrivingNotDetectedListener();
                                            break;
                                        case DROWSY_DRIVING_DETECTED:
                                            dnsRecognitionListener.drowsyDrivingDetectedListener();
                                            break;
                                    }
                                }
                            } catch (JSONException | ExecutionException | InterruptedException | SecurityException e) {
                                e.printStackTrace();
                            }

                            frames.clear();
                            stopConverting = false;
                        }).start();
                    } else if (frames.size() < 10) {
                        frames.add(modifedFrame);
                    }
                }

                return modifedFrame;
            }
        };
        cameraView.setCvCameraViewListener(cameraViewListener);

        onCameraPermissionGranted();
    }

    public void startRecognition() {
        cameraView.enableView();
    }

    public void stopRecognition() {
        cameraView.disableView();
    }

    /**
     * Add camera view into view group.
     *
     * @param viewGroup Target ViewGroup.
     */
    public void addCameraView(ViewGroup viewGroup) {
        viewGroup.addView(cameraView);
    }

    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = Collections.singletonList(cameraView);

        if (cameraViews == null) {
            Log.d("Check Camera", "No camera available");
            return;
        }

        Log.d("Check Camera", "Camera count: " + cameraViews.size());
        for (CameraBridgeViewBase cameraBridgeViewBase : cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    /**
     * Use this method inside your activity's onPause() function.
     */
    public void onPause() {
        if (cameraView != null) {
            cameraView.disableView();
            frames.clear();
        }
        if (frames != null) {
            frames.clear();
        }
    }

    /**
     * Use this method inside your activity's onDestroy() function.
     */
    public void onDestroy() {
        if (cameraView != null) {
            cameraView.disableView();
        }
        if (frames != null) {
            frames.clear();
        }
    }

    /**
     * Use this method inside your activity's onResume() function.
     */
    public void onResume() {
        if (!OpenCVLoader.initDebug()) {
            // Load OpenCV failed.
            Log.e("Load OpenCV", "Failed to load OpenCV");
        } else {
            // Load OpenCV success.
            Log.d("Load OpenCV", "OpenCV load successfully");
        }
    }
}