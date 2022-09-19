package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]


    @Builder
    private Order(Member member, List<OrderItem> orderItems, Delivery delivery, LocalDateTime orderDate, OrderStatus status) {
        this.member = member;
        this.delivery = delivery;
        this.orderDate = orderDate;
        this.status = status;

        for (OrderItem orderItem : orderItems) {
            addOrderItem(orderItem);
        }
    }

    //== 연관관계 메서드 (양방향일 경우) ==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.addOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
//        Order order = new Order();
//        order.setStatus(OrderStatus.ORDER);
//        order.setOrderDate(LocalDateTime.now());

        return Order.builder()
                .status(OrderStatus.ORDER)
                .member(member)
                .delivery(delivery)
                .orderItems(List.of(orderItems))
                .orderDate(LocalDateTime.now())
                .build();
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        status = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }


    //==조회 로직==//

    /**
     * 전체 주문가격 조회
     */
    public int getTotalPrice() {
        /*
         *        int totalPrice = 0;
         *        for (OrderItem orderItem : orderItems) {
         *            totalPrice += orderItem.getTotalPrice();
         *        }
         *
         *        return totalPrice;
         */
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
