package com.hiddenproject.compaj.core.model;

public interface DynamicFunction<T, R> {
  R apply(T... args);
}
