package com.paperized.easynotifier.dquery.func;

import com.paperized.easynotifier.dquery.BinaryConditionValues;
import com.paperized.easynotifier.dquery.DQueriable;
import com.paperized.easynotifier.dquery.utils.DQueryUtils;

import java.util.ArrayList;

public class GtCondition extends ArrayList<BinaryConditionValues> implements DQueryCondition {
    @Override
    public boolean evaluate(DQueriable qEntity) {
        for(BinaryConditionValues value : this) {
            Comparable leftValueCmp = (Comparable) DQueryUtils.getValueFromInput(value.getLeft(), qEntity);
            Object rightValue = DQueryUtils.getValueFromInput(value.getRight(), qEntity);
            DQueryUtils.requireSameType(leftValueCmp, rightValue);

            if(leftValueCmp.compareTo(rightValue) <= 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        for(BinaryConditionValues value : this) {
            Comparable leftValueCmp = (Comparable) DQueryUtils.getValueFromInput(value.getLeft(), qEntity1, qEntity2);
            Object rightValue = DQueryUtils.getValueFromInput(value.getRight(), qEntity1, qEntity2);
            DQueryUtils.requireSameType(leftValueCmp, rightValue);

            if(leftValueCmp.compareTo(rightValue) <= 0) {
                return false;
            }
        }

        return true;
    }
}
