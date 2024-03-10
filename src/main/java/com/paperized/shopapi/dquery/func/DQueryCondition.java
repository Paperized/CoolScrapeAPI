package com.paperized.shopapi.dquery.func;

import com.paperized.shopapi.dquery.DQueriable;

public interface DQueryCondition {
    boolean evaluate(DQueriable qEntity);
    boolean evaluate(DQueriable qEntity1, DQueriable qEntity2);

}
