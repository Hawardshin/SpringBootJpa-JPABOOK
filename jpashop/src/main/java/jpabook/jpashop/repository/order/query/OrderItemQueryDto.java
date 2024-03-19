package jpabook.jpashop.repository.order.query;

import lombok.Data;

@Data
public class OrderItemQueryDto {
    private Long orderId;
    private String itemName;
    private int oderPrice;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int oderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.oderPrice = oderPrice;
        this.count = count;
    }
}
