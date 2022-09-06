package com.dns.dns_lib;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;

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

    private BaseLoaderCallback baseLoaderCallback;

    /**
     * Selected camera type.
     */
    private int cameraType;

    /**
     * Original image received from the camera.
     */
    private Mat originalFrame;

    /**
     * Modified image with rotation and flip effects applied to make it look right on Android.
     */
    private Mat modifedFrame;

    private JavaCameraView cameraView;

    /**
     * Number of frames displayed in 1 second.
     */
    private int frameCount = 0;

    /**
     * Last frame check time.
     */
    private double lastFrameTime;

    private CameraBridgeViewBase.CvCameraViewListener2 cameraViewListener2;

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
     * DnsRecognition constructor.
     *
     * @param context    Application context.
     * @param cameraType Camera type(Back: 0, Front_Wide: 1, Front: 2)
     */
    public DnsRecognition(Context context, int cameraType) {
        this.cameraType = cameraType;
        cameraView = new JavaCameraView(context, cameraType);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        cameraView.setLayoutParams(layoutParams);

        cameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                originalFrame = new Mat(height, width, CvType.CV_8UC4);
                lastFrameTime = System.currentTimeMillis();
            }

            @Override
            public void onCameraViewStopped() {
                originalFrame.release();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                originalFrame = inputFrame.rgba();
                modifedFrame = originalFrame.t();

                // Check camera type and modify frame.
                if (cameraType == 0) {
                    // If selected camera type is back camera.
                    Core.flip(originalFrame.t(), modifedFrame, 1);
                } else if (cameraType == 1 || cameraType == 2) {
                    // If selected camera type is front camera.
                    Core.flip(originalFrame.t(), modifedFrame, -1);
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

                return modifedFrame;
            }
        };

        cameraView.setCvCameraViewListener(cameraViewListener2);

        onCameraPermissionGranted();

        baseLoaderCallback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        cameraView.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
        baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    /**
     * Get camera preview widget. It is recommended that this view be included in ConstraintLayout.
     *
     * @return Camera preview widget.
     */
    public JavaCameraView getCameraView() {
        return cameraView;
    }

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
     * Use this method inside your activity's onDestroy() function.
     */
    public void onDestroy() {
        if (cameraView != null) {
            cameraView.disableView();
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
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    /**
     * Use this method inside your activity's onPause() function.
     */
    public void onPause() {
        if (cameraView != null) {
            cameraView.disableView();
        }
    }
}
