package choreography.with.deadline.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import choreography.with.deadline.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import choreography.with.deadline.domain.*;


@Service
@Transactional
public class PolicyHandler{
    @Autowired DeliveryRepository deliveryRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderCreated'")
    public void wheneverOrderCreated_StartDelivery(@Payload OrderCreated orderCreated){

        OrderCreated event = orderCreated;
        System.out.println("\n\n##### listener StartDelivery : " + orderCreated + "\n\n");

        // Sample Logic //
        Delivery.startDelivery(event);
    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderRejected'")
    public void wheneverOrderRejected_Compensate(@Payload OrderRejected orderRejected){

        OrderRejected event = orderRejected;
        System.out.println("\n\n##### listener Compensate : " + orderRejected + "\n\n");

        // Sample Logic //
        Delivery.compensate(event);
    }

}

