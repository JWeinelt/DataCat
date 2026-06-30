package de.julianweinelt.datacat.dbx.api.ui;

import java.util.concurrent.CompletableFuture;

public interface ResponseDialog {
    CompletableFuture<Integer> answer();
    CompletableFuture<Void> closed();
}