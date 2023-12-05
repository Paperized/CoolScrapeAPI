package com.paperized.shopapi.dquery;

public interface DQueryCondition {
    boolean evaluate(DQueriable qEntity);
    boolean evaluate(DQueriable qEntity1, DQueriable qEntity2);
}
