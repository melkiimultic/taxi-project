package com.example.orderhistoryservice.mapper;

import com.example.orderhistoryservice.domain.HistoryEntry;
import com.example.orderhistoryservice.dto.OrderMsgDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntryDtoMapper {
    @Mapping( target = "orderId", source = "id")
    @Mapping(target = "id", ignore = true)
    HistoryEntry fromDTO(OrderMsgDTO orderMsgDTO);

    @Mapping( target = "id", source = "orderId")
    OrderMsgDTO toDTO(HistoryEntry historyEntry);

}
