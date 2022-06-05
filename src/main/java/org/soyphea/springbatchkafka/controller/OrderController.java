package org.soyphea.springbatchkafka.controller;

import lombok.AllArgsConstructor;
import org.soyphea.springbatchkafka.dto.TxOrderDto;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.soyphea.springbatchkafka.repository.TxOrderRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrderController {

    private final TxOrderRepository txOrderRepository;

    @PostMapping("/orders")
    public TxOrder create(@RequestBody TxOrderDto txOrderDto) {
        TxOrder txOrder = TxOrder.builder()
                .amount(txOrderDto.getAmount())
                .name(txOrderDto.getName())
                .remark(txOrderDto.getRemark())
                .status(txOrderDto.getStatus())
                .build();
        return txOrderRepository.save(txOrder);
    }
}
