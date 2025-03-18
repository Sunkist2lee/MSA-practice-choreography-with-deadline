package choreography.with.deadline.domain;

import choreography.with.deadline.domain.OrderCreated;
import choreography.with.deadline.domain.OrderPlaced;
import choreography.with.deadline.domain.OrderRejected;
import choreography.with.deadline.OrderApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Order_table")
@Data

public class Order  {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
        
    private String customerId;
    private String customerName;
    private String productId;
    private String productName;
    private Integer qty;
    private String address;
    private String status;

    @PrePersist
    public void setStatus(){
        setStatus("PENDING"); //FOCUS
    }
    
    @PostPersist
    public void onPostPersist() {
        OrderCreated orderCreated = new OrderCreated(this);
        orderCreated.publishAfterCommit();
    }

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }

    public static void approve(StockDecreased stockDecreased){
        repository().findById(Long.valueOf(stockDecreased.getOrderId())).ifPresent(order->{
            
            order.setStatus("APPROVED");
            repository().save(order);

            OrderPlaced orderPlaced = new OrderPlaced(order);
            orderPlaced.publishAfterCommit();
         });
    }

    public static void reject(DeliveryFailed deliveryFailed){
        repository().findById(Long.valueOf(deliveryFailed.getOrderId())).ifPresent(order->{
            
            order.setStatus("REJECTED DUE TO DELIVERY ERROR");
            repository().save(order);

            OrderRejected orderRejected = new OrderRejected(order);
            orderRejected.publishAfterCommit();
         });
    }

    public static void reject(StockDecreaseFailed stockDecreaseFailed){
        repository().findById(Long.valueOf(stockDecreaseFailed.getOrderId())).ifPresent(order->{
            
            order.setStatus("REJECTED DUE TO INVENTORY ERROR");
            repository().save(order);

            OrderRejected orderRejected = new OrderRejected(order);
            orderRejected.publishAfterCommit();
         });
    }

    public static void reject(DeadlineReached deadlineReached) {
        repository().findById(Long.valueOf(deadlineReached.getOrderId())).ifPresent(order->{
            
            order.setStatus("REJECTED DUE TO DEADLINE");
            repository().save(order);

            OrderRejected orderRejected = new OrderRejected(order);
            orderRejected.publishAfterCommit();
         });
    }


}
