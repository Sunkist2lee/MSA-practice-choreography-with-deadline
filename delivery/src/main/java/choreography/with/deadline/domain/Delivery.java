package choreography.with.deadline.domain;

import choreography.with.deadline.domain.DeliveryStarted;
import choreography.with.deadline.domain.DeliveryFailed;
import choreography.with.deadline.domain.DeliveryCancelled;
import choreography.with.deadline.DeliveryApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Delivery_table")
@Data

public class Delivery  {
    static final int deadlineDurationInMS = 10 * 1000;  //FOCUS: 데드라인 10초

    @Id
    // @GeneratedValue(strategy=GenerationType.AUTO) // FOCUS: disable auto-gen for key. 
    private String orderId;
    private String productId;
    private String productName;
    private Integer qty;
    private String customerId;
    private String address;    
    private String status;

    public static DeliveryRepository repository(){
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(DeliveryRepository.class);
        return deliveryRepository;
    }

    public static void startDelivery(OrderCreated orderCreated){

        //FOCUS: 임의로 처리 속도를 느리게 만든 구간 -- 주문된 상품번호가 1일 경우, 10초의 강제 딜레이를 발생시킨다.
        // if("1".equals(orderCreated.getProductId()))
        // try{
        //     Thread.sleep(10000);
        // }catch(Exception e){}

        // Date now = new Date();
        // if(orderCreated.getTimestamp() + deadlineDurationInMS < now.getTime()) return;  // FOCUS: skip the expired OrderCreated events

        Delivery delivery = new Delivery();

        delivery.setOrderId(String.valueOf(orderCreated.getId()));      // Prevent duplicate execution of the same message
        delivery.setCustomerId(orderCreated.getCustomerId());
        delivery.setProductId(orderCreated.getProductId());
        delivery.setProductName(orderCreated.getProductName());
        delivery.setQty(orderCreated.getQty());
        repository().save(delivery);

        DeliveryStarted deliveryStarted = new DeliveryStarted(delivery);
        deliveryStarted.publishAfterCommit();   
        
        // FOCUS:
        // if (some business reason, not a technical reason){
        //  DeliveryFailed deliveryFailed = new DeliveryFailed(delivery);
        //  deliveryFailed.publishAfterCommit();
        // }
    }

    public static void compensate(OrderRejected orderRejected){
        
        repository().findByOrderId(String.valueOf(orderRejected.getId())).ifPresent/*OrElse*/(delivery->{
            new DeliveryCancelled(delivery).publishAfterCommit();
            repository().delete(delivery);
         }
         
        //, ()->{
        //     throw new RuntimeException("No Delivery transaction is found for orderId : " + orderRejected.getId());
        //  }
         );

        
    }


}