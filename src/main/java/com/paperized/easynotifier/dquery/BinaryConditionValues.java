package com.paperized.easynotifier.dquery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BinaryConditionValues implements Serializable {
    private Object left;
    private Object right;
}
