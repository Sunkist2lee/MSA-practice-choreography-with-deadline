package choreographywithdeadline.domain;

import choreographywithdeadline.domain.*;
import choreographywithdeadline.infra.AbstractEvent;
import java.util.*;
import lombok.*;
import java.time.LocalDate;


//<<< DDD / Domain Event
@Data
@ToString
public class StockIncreased extends AbstractEvent {

    private Long id;
    private String productName;
    private String productImage;
    private Integer stock;
    private String orderId;

    public StockIncreased( aggregate){
        super(aggregate);
    }
    public StockIncreased(){
        super();
    }
}
//>>> DDD / Domain Event