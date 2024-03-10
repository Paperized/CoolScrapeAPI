package com.paperized.shopapi.dquery.func;

import com.paperized.shopapi.dquery.BinaryConditionValues;
import com.paperized.shopapi.dquery.DQueriable;
import com.paperized.shopapi.dquery.utils.DQueryUtils;

import java.util.ArrayList;

public class LtCondition extends ArrayList<BinaryConditionValues> implements DQueryCondition {
    @Override
    public boolean evaluate(DQueriable qEntity) {
        for(BinaryConditionValues value : this) {
            Comparable leftValueCmp = (Comparable) DQueryUtils.getValueFromInput(value.getLeft(), qEntity);
            Object rightValue = DQueryUtils.getValueFromInput(value.getRight(), qEntity);
            DQueryUtils.requireSameType(leftValueCmp, rightValue);

            if(leftValueCmp.compareTo(rightValue) >= 0) {
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

            if(leftValueCmp.compareTo(rightValue) >= 0) {
                return false;
            }
        }

        return true;
    }
}
