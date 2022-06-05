package org.soyphea.springbatchkafka.mapper;

import org.mapstruct.Mapper;
import org.soyphea.springbatchkafka.dto.TxOrderDto;
import org.soyphea.springbatchkafka.entity.TxOrder;

@Mapper(componentModel = "spring")
public interface TxOrderMapper {

	TxOrder fromTxOrderDto(TxOrderDto txOrderDto);

}
