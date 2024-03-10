package com.paperized.shopapi.dquery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinaryConditionValues {
    private Object left;
    private Object right;
}
