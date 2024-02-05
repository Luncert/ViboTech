package com.luncert.vibotech.common;

@FunctionalInterface
public interface ActionCallback {

    void accept(Object... data);
}
