
package com.bank.charge.model;

import java.math.BigDecimal;
import java.util.Map;

public class ChargeRule {

    private final String market;
    private final int priority;
    private final BigDecimal rate;
    private final Node root;

    public ChargeRule(String market, int priority,
                      BigDecimal rate, Node root) {
        this.market = market;
        this.priority = priority;
        this.rate = rate;
        this.root = root;
    }

    public boolean matches(Map<String, Object> context) {
        return root.evaluate(context);
    }

    public BigDecimal getRate() {
        return rate;
    }

    public int getPriority() {
        return priority;
    }
}
