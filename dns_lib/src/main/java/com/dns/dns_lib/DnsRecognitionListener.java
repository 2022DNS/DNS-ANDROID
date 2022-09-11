package com.dns.dns_lib;

public interface DnsRecognitionListener {
    void detectFaceLandmarksNotCorrectlyListener();

    void failedToDetectFaceLandmarksListener();

    void failedToDetectFaceListener();

    void drowsyDrivingNotDetectedListener();

    void drowsyDrivingDetectedListener();
}
