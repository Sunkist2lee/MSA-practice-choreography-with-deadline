package choreography.with.deadline.domain;

import choreography.with.deadline.domain.*;
import choreography.with.deadline.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class OrderCreated extends AbstractEvent {

    private Long id;
    private String customerId;
    private String customerName;
    private String productId;
    private String productName;
    private Integer qty;
    private String address;
    private String status;
    
    public OrderCreated(Order aggregate){
        super(aggregate);
    }
    public OrderCreated(){
        super();
    }
}
