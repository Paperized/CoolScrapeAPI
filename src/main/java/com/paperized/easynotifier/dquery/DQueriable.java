package com.paperized.easynotifier.dquery;

public interface DQueriable {
    Object getVariableValue(String name);
    String getUniqueIdentifier();
    String calculateAndSetUniqueIdentifier();
}
