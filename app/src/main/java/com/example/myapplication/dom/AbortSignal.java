package com.example.myapplication.dom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AbortSignal {
    private boolean aborted;
    private List<Consumer<Void>> abortListeners;

    public AbortSignal() {
        this.aborted = false;
        this.abortListeners = new ArrayList<>();
    }

    public boolean isAborted() {
        return aborted;
    }

    public void abort() {
        this.aborted = true;
        for (Consumer<Void> listener : abortListeners) {
            listener.accept(null);
        }
    }

    public void addEventListener(String type, Consumer<Void> listener) {
        if ("abort".equals(type)) {
            abortListeners.add(listener);
        }
    }
}
