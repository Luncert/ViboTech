package com.luncert.vibotech.compat.vibotech.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TickOrder {

  /**
   * Bigger value lower priority.
   */
  int value();
}
