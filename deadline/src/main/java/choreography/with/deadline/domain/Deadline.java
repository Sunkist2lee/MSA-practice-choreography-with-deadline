package choreography.with.deadline.domain;

import choreography.with.deadline.domain.DeadlineReached;
import choreography.with.deadline.DeadlineApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Deadline_table")
@Data

public class Deadline  {
    static final int deadlineDurationInMS = 10 * 1000;  //FOCUS: 데드라인 10초

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;       
    private Date deadline;
    private Long orderId;
    private Date startedTime;

    public static DeadlineRepository repository(){
        DeadlineRepository deadlineRepository = DeadlineApplication.applicationContext.getBean(DeadlineRepository.class);
        return deadlineRepository;
    }

    public static void schedule(OrderCreated orderCreated){
        Deadline deadline = new Deadline();
        deadline.setOrderId(orderCreated.getId());
        deadline.setStartedTime(new Date(orderCreated.getTimestamp()));

        Date deadlineDate = new Date(deadline.getStartedTime().getTime() + deadlineDurationInMS);
        deadline.setDeadline(deadlineDate);
        
        repository().save(deadline);
    }

    public static void delete(OrderPlaced orderPlaced) {
        repository().findByOrderId(orderPlaced.getId()).ifPresentOrElse(deadline ->{
            repository().delete(deadline);
        }, ()->{throw new RuntimeException("No such order id :" + orderPlaced.getId());});

    }
    
    public static void sendDeadlineEvents(){
        repository().findAll().forEach(deadline ->{
            Date now = new Date();
            
            if(now.after(deadline.getDeadline())){
                repository().delete(deadline);   
                new DeadlineReached(deadline).publishAfterCommit();
            }
        });
    }

}
