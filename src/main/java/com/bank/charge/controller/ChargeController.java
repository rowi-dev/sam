
package com.bank.charge.controller;

import com.bank.charge.service.RuleEngineService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/charge")
public class ChargeController {

    private final RuleEngineService service;

    public ChargeController(RuleEngineService service) {
        this.service = service;
    }

    @PostMapping("/{market}")
    public BigDecimal calculate(@PathVariable String market,
                                @RequestBody String payload) throws Exception {
        return service.calculate(market, payload);
    }
}
