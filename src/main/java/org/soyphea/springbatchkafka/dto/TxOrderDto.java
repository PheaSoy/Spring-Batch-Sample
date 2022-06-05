package org.soyphea.springbatchkafka.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TxOrderDto implements Serializable {
    private final String name;
    private final Double amount;
    private final String remark;
    private final String status;
}
