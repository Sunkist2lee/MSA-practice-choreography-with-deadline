package choreography.with.deadline.infra;

// import javax.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import choreography.with.deadline.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


import choreography.with.deadline.domain.*;

@Service
@Transactional
public class PolicyHandler{
    @Autowired DeadlineRepository deadlineRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderCreated'")
    public void wheneverOrderCreated_Schedule(@Payload OrderCreated orderCreated){

        OrderCreated event = orderCreated;

        // Sample Logic //
        Deadline.schedule(event);
    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderPlaced'")
    public void wheneverOrderPlaced_delete(@Payload OrderPlaced orderPlaced){

        OrderPlaced event = orderPlaced;

        // Sample Logic //
        Deadline.delete(event);       
    }

    // @Scheduled(fixedRate = 5000) 간혹, Unexpected error occurred in scheduled task 오류 발생.. @Scheduled @Transactional 분리권고에 따라 DeadlineScheduler 추가 
    public void checkDeadline(){
        Deadline.sendDeadlineEvents();
    }

}


