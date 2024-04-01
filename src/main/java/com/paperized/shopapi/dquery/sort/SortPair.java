package com.paperized.shopapi.dquery.sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortPair implements Serializable {
    private String by;
    private SortDirection dir;

    public enum SortDirection {
        ASC, DESC
    }
}
