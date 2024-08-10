package com.paperized.easynotifier.dquery.func;

import com.paperized.easynotifier.dquery.DQueriable;

public interface DQueryCondition {
    boolean evaluate(DQueriable qEntity);
    boolean evaluate(DQueriable qEntity1, DQueriable qEntity2);

}
