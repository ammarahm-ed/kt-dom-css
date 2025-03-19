package com.example.myapplication.dom;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Event {
    public static final int NONE = 0;
    public static final int CAPTURING_PHASE = 1;
    public static final int AT_TARGET = 2;
    public static final int BUBBLING_PHASE = 3;

    private final String type;
    public final boolean bubbles;
    public final boolean cancelable;
    public final boolean composed;
    public EventTarget currentTarget;
    private boolean defaultPrevented;
    public final boolean isTrusted;
    public EventTarget target;
    public final long timeStamp;
    public boolean cancelBubble;
    private boolean returnValue;
    public boolean directEvent;
    public int eventPhase;

    public static Event createEvent(String type, @Nullable EventInit eventInitDict) {
        return new Event(type, new EventInit());
    }

    public Event(String type, @Nullable EventInit eventInitDict) {
        this.type = type;
        this.bubbles = eventInitDict.bubbles != null ? eventInitDict.bubbles : false;
        this.cancelable = eventInitDict.cancelable != null ? eventInitDict.cancelable : false;
        this.composed = eventInitDict.composed != null ? eventInitDict.composed : false;
        this.currentTarget = null;
        this.defaultPrevented = false;
        this.eventPhase = NONE;
        this.isTrusted = false;
        this.target = null;
        this.timeStamp = System.currentTimeMillis();
        this.cancelBubble = false;
        this.returnValue = true;
        this.directEvent = eventInitDict.directEvent != null ? eventInitDict.directEvent : false;
    }

    public boolean isBubbles() {
        return bubbles;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public boolean isComposed() {
        return composed;
    }

    public EventTarget getCurrentTarget() {
        return currentTarget;
    }

    public boolean isDefaultPrevented() {
        return defaultPrevented;
    }

    public int getEventPhase() {
        return eventPhase;
    }

    public boolean isTrusted() {
        return isTrusted;
    }

    public EventTarget getTarget() {
        return target;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getType() {
        return type;
    }

    public List<EventTarget> composedPath() {
        List<EventTarget> path = new ArrayList<>();
        Node current = (Node) this.currentTarget;
        while (current != null) {
            path.add(current);
            current = current.getParentNode(); // Assuming getParent() method exists in EventTarget
        }
        return path;
    }

    public void initEvent(String type, boolean bubbles, boolean cancelable) {
        // Deprecated method, typically no-op in modern implementations
    }

    public void preventDefault() {
        if (cancelable) {
            defaultPrevented = true;
        }
    }

    public void stopImmediatePropagation() {
        cancelBubble = true;
    }

    public void stopPropagation() {
        cancelBubble = true;
    }


}
