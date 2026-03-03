
package com.bank.charge.model;

import com.bank.charge.util.FieldExtractor;
import java.util.*;

public class ConditionNode implements Node {

    private final String[] pathTokens;
    private final Operator operator;
    private final String expected;

    public ConditionNode(String fieldPath, Operator operator, String expected) {
        this.pathTokens = fieldPath.replaceAll("\\[.*?\\]", "")
                .replaceAll("\"", "")
                .split("\\.");
        this.operator = operator;
        this.expected = expected.replace("'", "");
    }

    @Override
    public boolean evaluate(Map<String, Object> context) {

        List<Object> extracted =
                FieldExtractor.extractAll(context, pathTokens);

        if (extracted.isEmpty()) return false;

        for (Object val : extracted) {
            String actual = String.valueOf(val);
            switch (operator) {
                case EQ: if (actual.equals(expected)) return true; break;
                case NE: if (!actual.equals(expected)) return true; break;
            }
        }
        return false;
    }
}
