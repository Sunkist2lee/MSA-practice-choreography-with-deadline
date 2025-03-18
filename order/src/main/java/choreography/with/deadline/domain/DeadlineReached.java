package choreography.with.deadline.domain;

import choreography.with.deadline.domain.*;
import choreography.with.deadline.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class DeadlineReached extends AbstractEvent {

    private Long id;
    private Date deadline;
    private Long orderId;
    private Date startedTime;
}


