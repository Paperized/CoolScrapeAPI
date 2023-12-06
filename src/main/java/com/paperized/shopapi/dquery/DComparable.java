package com.paperized.shopapi.dquery;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;

import static java.lang.String.format;

@Data
@AllArgsConstructor
public class DComparable implements Comparator<DQueriable> {
    private DSort sortPairs;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public int compare(DQueriable o1, DQueriable o2) {
        for (SortPair currPair : sortPairs) {
            DQueriable curr1 = o1;
            DQueriable curr2 = o2;
            if (currPair.getDir() == null || currPair.getDir().equals(SortPair.SortDirection.DESC)) {
                DQueriable temp = curr1;
                curr1 = curr2;
                curr2 = temp;
            }

            Object value1 = curr1.getVariableValue(currPair.getBy());
            Object value2 = curr2.getVariableValue(currPair.getBy());

            // null is sorter after a real value
            if(value1 == null) {
                if(value2 == null)
                    continue;

                return 1;
            }

            if (value1 instanceof Comparable valueComparable1) {
                if(value2 == null) {
                    return -1;
                }

                int currComp = valueComparable1.compareTo(value2);
                if (currComp != 0) {
                    return currComp;
                } else {
                    continue;
                }
            }

            throw new RuntimeException(format("%s parameter name does not implements comparable, type: %s", currPair.getBy(), value1.getClass().getSimpleName()));
        }

        return 0;
    }

    public static DComparable fromDSort(DSort sort) {
        return new DComparable(sort);
    }
}
