package com.paperized.easynotifier.dquery.func;

import com.paperized.easynotifier.dquery.BinaryConditionValues;
import com.paperized.easynotifier.dquery.DQueriable;
import com.paperized.easynotifier.dquery.utils.DQueryUtils;

import java.util.ArrayList;
import java.util.Objects;

public class EqualsCondition extends ArrayList<BinaryConditionValues> implements DQueryCondition {
    @Override
    public boolean evaluate(DQueriable qEntity) {
        for(BinaryConditionValues value : this) {
            boolean isEq = Objects.equals(
                    DQueryUtils.getValueFromInput(value.getLeft(), qEntity),
                    DQueryUtils.getValueFromInput(value.getRight(), qEntity));
            if(!isEq) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        for(BinaryConditionValues value : this) {
            boolean isEq = Objects.equals(
                    DQueryUtils.getValueFromInput(value.getLeft(), qEntity1, qEntity2),
                    DQueryUtils.getValueFromInput(value.getRight(), qEntity1, qEntity2));
            if(!isEq) {
                return false;
            }
        }

        return true;
    }
}
