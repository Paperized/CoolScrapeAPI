package com.paperized.shopapi.dquery;

import lombok.Data;

@Data
public class QueryCondition implements DQueryCondition {
    private EqualsCondition equals;
    private OrCondition or;
    private AndCondition and;

    @Override
    public boolean evaluate(DQueriable qEntity) {
        if(equals != null && !equals.evaluate(qEntity))
            return false;
        if(or != null && !or.evaluate(qEntity))
            return false;
        return and == null || !and.evaluate(qEntity);
    }

    @Override
    public boolean evaluate(DQueriable qEntity1, DQueriable qEntity2) {
        if(!equals.evaluate(qEntity1, qEntity2))
            return false;
        if(!or.evaluate(qEntity1, qEntity2))
            return false;
        return and == null || !and.evaluate(qEntity1, qEntity2);
    }

    public QueryCondition addEquals(BinaryConditionValues eq) {
        if(equals == null)
            equals = new EqualsCondition();

        equals.add(eq);
        return this;
    }

    public QueryCondition addAndCondition(QueryCondition condition) {
        if(and == null)
            and = new AndCondition();

        and.add(condition);
        return this;
    }

    public QueryCondition addOrCondition(QueryCondition condition) {
        if(or == null)
            or = new OrCondition();

        or.add(condition);
        return this;
    }


}
