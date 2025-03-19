package com.example.myapplication.dom;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventTarget {
    private final Map<String, List<EventListenerEntry>> listeners = new HashMap<>();

    public void addEventListener(String type, EventListener listener, AddEventListenerOptions options) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(new EventListenerEntry(listener, options));
        if (options != null && options.signal != null) {
            options.signal.addEventListener("abort", (v) -> removeEventListener(type, listener, null));
        }
    }

    public void removeEventListener(String type, EventListener listener, @Nullable AddEventListenerOptions options) {
        List<EventListenerEntry> entries = listeners.get(type);
        if (entries != null) {
            entries.removeIf(entry -> entry.listener.equals(listener) && entry.options.capture == options.capture);
        }
    }

    public boolean dispatchEvent(Event event) {
        List<EventTarget> path = event.composedPath();

        if (event.directEvent) {
            event.eventPhase = Event.AT_TARGET;
            this.invokeListeners(event, false);
            return !event.isDefaultPrevented();
        }

        event.eventPhase = Event.CAPTURING_PHASE;

        // Capturing phase
        for (int i = path.size() - 1; i >= 0; i--) {
            EventTarget target = path.get(i);

            target.invokeListeners(event, true);
            if (event.cancelBubble) {
                return !event.isDefaultPrevented();
            }
        }

        // At target
        event.eventPhase = Event.AT_TARGET;
        this.invokeListeners(event, false);
        if (event.cancelBubble) {
            return !event.isDefaultPrevented();
        }

        // Bubbling phase
        if (event.isBubbles()) {
            event.eventPhase = Event.BUBBLING_PHASE;
            for (EventTarget target : path) {
                target.invokeListeners(event, false);
                if (event.cancelBubble) {
                    return !event.isDefaultPrevented();
                }
            }
        }

        return !event.isDefaultPrevented();
    }

    private void invokeListeners(Event event, boolean capture) {
        List<EventListenerEntry> entries = listeners.get(event.getType());
        if (entries != null) {
            for (EventListenerEntry entry : entries) {
                if (entry.options.capture == capture && !entry.options.passive) {
                    if (entry.listener != null) {
                        ((EventListener) entry.listener).handleEvent(event);
                    }

                    if (entry.options.once) {
                        this.removeEventListener(event.getType(), (EventListener) entry.listener, entry.options);

                    }
                }
            }
        }
    }

    private static class EventListenerEntry {
        EventListener listener;
        AddEventListenerOptions options;

        EventListenerEntry(EventListener listener, AddEventListenerOptions options) {
            this.listener = listener;
            this.options = options;
        }
    }
}
