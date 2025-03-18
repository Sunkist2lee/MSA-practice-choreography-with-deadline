package choreography.with.deadline.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class DeadlineScheduler {
    @Autowired PolicyHandler policyHandler;

    @Scheduled(fixedRate = 5000) //FOCUS: every 5 seconds. 5초에 한번씩
    public void checkDeadline(){
        policyHandler.checkDeadline();
    }
}
