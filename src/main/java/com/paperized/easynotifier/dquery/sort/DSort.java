package com.paperized.easynotifier.dquery.sort;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class DSort extends ArrayList<SortPair> {
    public DSort addSort(String by, SortPair.SortDirection dir) {
        if(StringUtils.isBlank(by))
            return this;

        add(new SortPair(by, dir == null ? SortPair.SortDirection.ASC : dir));
        return this;
    }
}
