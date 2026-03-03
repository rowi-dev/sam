
package com.bank.charge.engine;

import com.bank.charge.model.*;
import com.bank.charge.parser.SimpleRuleParser;
import org.apache.commons.csv.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class RuleLoader {

    private final Map<String, List<ChargeRule>> rulesByMarket = new HashMap<>();
    private final SimpleRuleParser parser = new SimpleRuleParser();

    @PostConstruct
    public void load() throws Exception {

        var resource = new ClassPathResource("rules.csv");

        try (var reader = new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8)) {

            CSVParser csv = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord r : csv) {

                String market = r.get("market");
                int priority = Integer.parseInt(r.get("priority"));
                BigDecimal rate = new BigDecimal(r.get("rate"));
                String expression = r.get("expression");

                Node root = parser.parse(expression);

                ChargeRule rule =
                        new ChargeRule(market, priority, rate, root);

                rulesByMarket
                        .computeIfAbsent(market, k -> new ArrayList<>())
                        .add(rule);
            }
        }
    }

    public Map<String, List<ChargeRule>> getRules() {
        return rulesByMarket;
    }
}
