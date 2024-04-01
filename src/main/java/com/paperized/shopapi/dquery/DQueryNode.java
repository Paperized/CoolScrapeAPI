package com.paperized.shopapi.dquery;

import com.paperized.shopapi.dquery.func.*;
import lombok.Data;

import java.io.Serializable;

@Data
public class DQueryNode implements DQueryCondition, Serializable {
    private NotCondition not;
    private OrCondition or;

    private EqualsCondition eq;
    private GtCondition gt;
    private GteCondition gte;
    private LtCondition lt;
    private LteCondition lte;
    private EmptyCondition empty;

    @Override
    public boolean evaluate(DQueriable qEntity) {
        if(eq != null && !eq.evaluate(qEntity))
            return false;
        if(gt != null && !gt.evaluate(qEntity))
            return false;
        if(gte != null && !gte.evaluate(qEntity))
            return false;
        if(lt != null && !lt.evaluate(qEntity))
            return false;
        if(lte != null && !lte.evaluate(qEntity))
            return false;
        if(empty != null && !empty.evaluate(qEntity))
            return false;
        if(not != null && !not.evaluate(qEntity))
            return false;
        return or == null || or.evaluate(qEntity);
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        if(eq != null && !eq.evaluate(qEntity1, qEntity2))
            return false;
        if(gt != null && !gt.evaluate(qEntity1, qEntity2))
            return false;
        if(gte != null && !gte.evaluate(qEntity1, qEntity2))
            return false;
        if(lt != null && !lt.evaluate(qEntity1, qEntity2))
            return false;
        if(lte != null && !lte.evaluate(qEntity1, qEntity2))
            return false;
        if(empty != null && !empty.evaluate(qEntity1, qEntity2))
            return false;
        if(not != null && !not.evaluate(qEntity1, qEntity2))
            return false;
        return or == null || or.evaluate(qEntity1, qEntity2);
    }
}
