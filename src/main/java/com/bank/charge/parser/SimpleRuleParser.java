
package com.bank.charge.parser;

import com.bank.charge.model.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleRuleParser {

    public Node parse(String expression) {

        expression = expression.trim();

        // Handle AND
        List<String> andParts = splitTopLevel(expression, "&&");
        if (andParts.size() > 1) {
            List<Node> nodes = new ArrayList<>();
            for (String part : andParts) {
                nodes.add(parse(part));
            }
            return new LogicalNode(LogicalOperator.AND, nodes);
        }

        // Handle OR
        List<String> orParts = splitTopLevel(expression, "||");
        if (orParts.size() > 1) {
            List<Node> nodes = new ArrayList<>();
            for (String part : orParts) {
                nodes.add(parse(part));
            }
            return new LogicalNode(LogicalOperator.OR, nodes);
        }

        // Condition
        if (expression.contains("==")) {
            String[] parts = expression.split("==");
            return new ConditionNode(parts[0].trim(),
                    Operator.EQ,
                    parts[1].trim());
        }

        if (expression.contains("!=")) {
            String[] parts = expression.split("!=");
            return new ConditionNode(parts[0].trim(),
                    Operator.NE,
                    parts[1].trim());
        }

        throw new RuntimeException("Unsupported expression: " + expression);
    }

    private List<String> splitTopLevel(String expression, String operator) {

        List<String> result = new ArrayList<>();
        int level = 0;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') level++;
            if (c == ')') level--;

            if (level == 0 && expression.startsWith(operator, i)) {
                result.add(current.toString());
                current.setLength(0);
                i += operator.length() - 1;
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result;
    }
}
