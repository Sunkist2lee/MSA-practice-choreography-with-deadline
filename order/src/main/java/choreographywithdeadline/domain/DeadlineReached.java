package choreographywithdeadline.domain;

import choreographywithdeadline.domain.*;
import choreographywithdeadline.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class DeadlineReached extends AbstractEvent {

    private Long id;
    private Date deadline;
    private Long orderId;
    private Date startedTime;
}
