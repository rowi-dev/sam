
package com.bank.charge.model;

import java.util.*;

public class LogicalNode implements Node {

    private final LogicalOperator operator;
    private final List<Node> children;

    public LogicalNode(LogicalOperator operator, List<Node> children) {
        this.operator = operator;
        this.children = children;
    }

    @Override
    public boolean evaluate(Map<String, Object> context) {

        if (operator == LogicalOperator.AND) {
            for (Node n : children) {
                if (!n.evaluate(context)) return false;
            }
            return true;
        } else {
            for (Node n : children) {
                if (n.evaluate(context)) return true;
            }
            return false;
        }
    }
}
