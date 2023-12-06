package com.paperized.shopapi.dquery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortPair {
    private String by;
    private SortDirection dir;

    public enum SortDirection {
        ASC, DESC
    }
}
