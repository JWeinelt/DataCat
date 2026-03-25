package de.julianweinelt.datacat.dbx.backup;

public interface ImportListener {
    void onProgress(int current, int total);
    void message(String message);
    void onLog(String message);
    void onError(String message, Throwable throwable);
}