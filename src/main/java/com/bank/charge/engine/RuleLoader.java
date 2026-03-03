
package com.bank.charge.engine;

import com.bank.charge.model.*;
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

    @PostConstruct
    public void load() throws Exception {

        var resource = new ClassPathResource("rules.csv");

        try (var reader = new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8)) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord r : parser) {

                String market = r.get("market");
                int priority = Integer.parseInt(r.get("priority"));
                BigDecimal rate = new BigDecimal(r.get("rate"));

                // Advanced Rule Example (Structured)
                Node ruleTree =
                        new LogicalNode(LogicalOperator.AND, List.of(

                            new ConditionNode("data.cbs.enqryInf.CHRG.casaSgmntIdr",
                                    Operator.EQ, Set.of("R")),

                            new ConditionNode("header.pmtTp",
                                    Operator.EQ, Set.of("TT")),

                            new LogicalNode(LogicalOperator.OR, List.of(
                                    new ConditionNode("header.subPmtTp",
                                            Operator.EQ, Set.of("OT")),
                                    new ConditionNode("header.subPmtTp",
                                            Operator.EQ, Set.of("OL"))
                            )),

                            new ConditionNode("data.chrgsInf.chrgBr",
                                    Operator.EQ, Set.of("DEBT")),

                            new LogicalNode(LogicalOperator.OR, List.of(
                                    new ConditionNode("data.chanl.instgChanl",
                                            Operator.EQ, Set.of("IBK")),
                                    new ConditionNode("data.chanl.instgChanl",
                                            Operator.EQ, Set.of("MBK"))
                            )),

                            new LogicalNode(LogicalOperator.OR, List.of(
                                    new ConditionNode("data.amt.instdCcy",
                                            Operator.EQ, Set.of("SGD")),
                                    new ConditionNode("data.amt.instdCcy",
                                            Operator.EQ, Set.of("USD")),
                                    new ConditionNode("data.amt.instdCcy",
                                            Operator.EQ, Set.of("EUR"))
                            )),

                            new ConditionNode("data.cbs.enqryInf.CHRG.rltnshpTp",
                                    Operator.NE, Set.of("N")),

                            new ConditionNode("header.cdtDbtInd",
                                    Operator.EQ, Set.of("CRDT"))
                        ));

                ChargeRule rule = new ChargeRule(
                        market, priority, rate, ruleTree);

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
