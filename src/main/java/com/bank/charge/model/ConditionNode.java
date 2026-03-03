
package com.bank.charge.model;

import com.bank.charge.util.FieldExtractor;
import java.util.*;

public class ConditionNode implements Node {

    private final String[] pathTokens;
    private final Operator operator;
    private final Set<String> values;

    public ConditionNode(String fieldPath, Operator operator, Set<String> values) {
        this.pathTokens = fieldPath.split("\\.");
        this.operator = operator;
        this.values = values;
    }

    @Override
    public boolean evaluate(Map<String, Object> context) {

        List<Object> extracted =
                FieldExtractor.extractAll(context, pathTokens);

        if (extracted.isEmpty()) return false;

        for (Object val : extracted) {
            String actual = String.valueOf(val);

            switch (operator) {
                case EQ:
                    if (values.contains(actual)) return true;
                    break;
                case NE:
                    if (!values.contains(actual)) return true;
                    break;
                case IN:
                    if (values.contains(actual)) return true;
                    break;
            }
        }
        return false;
    }
}
