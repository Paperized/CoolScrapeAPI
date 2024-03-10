package com.paperized.shopapi.dquery.func;

import com.paperized.shopapi.dquery.DQueriable;
import com.paperized.shopapi.dquery.utils.DQueryUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class EmptyCondition extends ArrayList<String> implements DQueryCondition {
    @Override
    public boolean evaluate(DQueriable qEntity) {
        for(String value : this) {
            if(!DQueryUtils.containsQueriableVariable(value)) {
                throw new RuntimeException(value + " is not a field inside the returned object!");
            }

            Object actualValue = DQueryUtils.getValueFromInput(value, qEntity);
            if(actualValue == null || (actualValue instanceof String str && StringUtils.isBlank(str))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        for(String value : this) {
            if(!DQueryUtils.containsQueriableVariable(value)) {
                throw new RuntimeException(value + " is not a field inside the returned object!");
            }

            Object actualValue = DQueryUtils.getValueFromInput(value, qEntity1, qEntity2);
            if(actualValue == null || (actualValue instanceof String str && StringUtils.isBlank(str))) {
                return false;
            }
        }

        return true;
    }
}
