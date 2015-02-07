package com.global.hbc.upyun;
public interface ProgressListener {
    void transferred(long transferedBytes, long totalBytes);
}