
package com.bank.charge.service;

import com.bank.charge.engine.RuleLoader;
import com.bank.charge.model.ChargeRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class RuleEngineService {

    private final RuleLoader loader;
    private final ObjectMapper mapper = new ObjectMapper();

    public RuleEngineService(RuleLoader loader) {
        this.loader = loader;
    }

    public BigDecimal calculate(String market, String json) throws Exception {

        Map<String, Object> payload =
                mapper.readValue(json, Map.class);

        List<ChargeRule> rules =
                loader.getRules().get(market);

        if (rules == null) return BigDecimal.ZERO;

        rules.sort(Comparator.comparingInt(ChargeRule::getPriority));

        for (ChargeRule rule : rules) {
            if (rule.matches(payload)) {
                return rule.getRate();
            }
        }

        return BigDecimal.ZERO;
    }
}
