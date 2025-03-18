package choreography.with.deadline.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import choreography.with.deadline.ProductApplication;
import lombok.Data;

@Entity
@Data
public class Transaction {
    @Id
    Long orderId;
    Integer stockOrdered;
    String customerId;

    public static TransactionRepository repository(){
        return ProductApplication.applicationContext.getBean(TransactionRepository.class);
    }
}