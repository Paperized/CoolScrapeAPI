package com.paperized.shopapi.dquery;

import java.util.ArrayList;
import java.util.Objects;

public class EqualsCondition extends ArrayList<BinaryConditionValues> implements DQueryCondition {
    @Override
    public boolean evaluate(DQueriable qEntity) {
        for(BinaryConditionValues value : this) {
            boolean isEq = Objects.equals(qEntity.getVariableValue(value.getVar()), value.getValue());
            if(!isEq) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        for(BinaryConditionValues value : this) {
            boolean isEq = Objects.equals(qEntity1.getVariableValue(value.getVar()), value.getValue());
            if(!isEq) {
                return false;
            }
        }

        return true;
    }

    public EqualsCondition addBinaryCondition(String var, Object value) {
        add(new BinaryConditionValues(var, value));
        return this;
    }

    public static EqualsCondition fromBinaryCondition(String var, Object value) {
        return new EqualsCondition().addBinaryCondition(var, value);
    }

    public QueryCondition toQueryCondition() {
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setEquals(this);
        return queryCondition;
    }
}
