package com.paperized.easynotifier.dquery.func;

import com.paperized.easynotifier.dquery.DQueriable;
import com.paperized.easynotifier.dquery.DQueryNode;

import java.util.ArrayList;

public class OrCondition extends ArrayList<DQueryNode> implements DQueryCondition {
    @Override
    public boolean evaluate(DQueriable qEntity) {
        for(DQueryCondition branch : this) {
            if(branch.evaluate(qEntity)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        for(DQueryCondition branch : this) {
            if(branch.evaluate(qEntity1, qEntity2)) {
                return true;
            }
        }

        return false;
    }
}