package com.paperized.easynotifier.dquery.utils;

import com.paperized.easynotifier.dquery.DQueriable;
import com.paperized.easynotifier.exceptions.DQueryNotSameTypeException;

public class DQueryUtils {

    public static Object getValueFromInput(Object valueInput, DQueriable v1) {
        return getValueFromInput(valueInput, v1, null);
    }

    public static Object getValueFromInput(Object valueInput, DQueriable v1, DQueriable v2) {
        if(!(valueInput instanceof String valueStr)) {
            return valueInput;
        }

        if(valueStr.startsWith("#")) {
            int parenthesisIndex = 1;
            DQueriable data = v1;
            if(valueStr.startsWith("##")) {
                data = v2;
                parenthesisIndex = 2;
            }

            if(valueStr.charAt(parenthesisIndex) == '(' && valueStr.endsWith(")")) {
                return data.getVariableValue(valueStr.substring(parenthesisIndex + 1, valueStr.length() - 1));
            }
        }

        return valueInput;
    }

    public static boolean containsQueriableVariable(String field) {
        if(field.startsWith("#")) {
            int parenthesisIndex = 1;
            if(field.startsWith("##")) {
                parenthesisIndex = 2;
            }

            return field.charAt(parenthesisIndex) == '(' && field.endsWith(")");
        }

        return false;
    }

    public static void requireSameType(Object a, Object b) {
        if(a.getClass().equals(b.getClass())) {
            return;
        }

        throw new DQueryNotSameTypeException("Type " + a.getClass().getSimpleName() + " is not compatible with: " + b.getClass().getSimpleName());
    }
}
