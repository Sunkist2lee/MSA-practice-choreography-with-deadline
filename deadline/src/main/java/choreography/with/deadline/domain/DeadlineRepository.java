package choreography.with.deadline.domain;

import choreography.with.deadline.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel="deadlines", path="deadlines")
public interface DeadlineRepository extends PagingAndSortingRepository<Deadline, Long>{

    Optional<Deadline> findByOrderId(Long id);
    
}
