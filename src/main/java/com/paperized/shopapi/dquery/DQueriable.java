package com.paperized.shopapi.dquery;

public interface DQueriable {
    Object getVariableValue(String name);
    String getUniqueIdentifier();
}
