package com.dns.dns_lib;

public interface DnsWakerListener {
    void onDriverPassedTestSuccessfully();

    void onDriverPassedTestFailed();
}
