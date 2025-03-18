package choreography.with.deadline.domain;

import choreography.with.deadline.domain.StockDecreaseFailed;
import choreography.with.deadline.domain.StockDecreased;
import choreography.with.deadline.domain.StockIncreased;
import choreography.with.deadline.ProductApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Inventory_table")
@Data

public class Inventory  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String productName;
    private String productImage;
    private Integer stock;

     public static InventoryRepository repository(){
        InventoryRepository inventoryRepository = ProductApplication.applicationContext.getBean(InventoryRepository.class);
        return inventoryRepository;
    }

    public static void stockDecrease(DeliveryStarted deliveryStarted){

        //FOCUS: 멱등성 관리. 한번 처리된 적이 있다면 스킵. handle idempotent: once processed, skip the process:
        if(Transaction.repository().findById(Long.valueOf(deliveryStarted.getOrderId())).isPresent())
         return;

        repository().findById(Long.valueOf(deliveryStarted.getProductId())).ifPresent(inventory->{
            
            if(inventory.getStock() > deliveryStarted.getQty()) {
                inventory.setStock(inventory.getStock() - deliveryStarted.getQty());
                repository().save(inventory);

                // 멱등성 처리를 위해 처리된 주문에 대해 Trx 범위 내에서 플래그 처리
                Transaction transaction = new Transaction();
                transaction.setOrderId(Long.valueOf(deliveryStarted.getOrderId()));
                transaction.setStockOrdered(deliveryStarted.getQty());
                transaction.setCustomerId(deliveryStarted.getCustomerId());
                Transaction.repository().save(transaction);

                StockDecreased stockDecreased = new StockDecreased(inventory);
                stockDecreased.setOrderId(deliveryStarted.getOrderId());
                stockDecreased.publishAfterCommit();
            } else {
                StockDecreaseFailed stockDecreaseFailed = new StockDecreaseFailed(inventory);
                stockDecreaseFailed.setOrderId(deliveryStarted.getOrderId());
                stockDecreaseFailed.publishAfterCommit();
            }

         });
    }
    
    public static void compensate(DeliveryCancelled deliveryCancelled){
        Transaction.repository().findById(Long.valueOf(deliveryCancelled.getOrderId())).ifPresentOrElse(tx ->{
            repository().findById(Long.valueOf(deliveryCancelled.getProductId())).ifPresent(inventory->{
                
                inventory.setStock(inventory.getStock() + deliveryCancelled.getQty()); // do something
                repository().save(inventory);

                Transaction.repository().delete(tx); //FOCUS: 멱등성 관리를 위해 두번 보상 처리되는 것을 막기 위해 트랜잭션 이력 삭제, (플래그로 처리해도 되긴 함).  handle idempotent. delete to prevent to process twice
                new StockIncreased(inventory).publish();

            });
        }
         ,()->{
             throw new RuntimeException("Compensation failed due to stock");
         }
        );
    }
}
