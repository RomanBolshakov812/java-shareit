package ru.practicum.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    Page<Request> findRequestsByRequestorIdOrderByCreatedDesc(Integer requestorId,
                                                              Pageable pageable);

    @Query(nativeQuery = true, value = "select * from requests  "
            + "where requests.requestor_id <> ?1 order by requests.created desc")
    Page<Request> findRequestsByOtherUsers(Integer requestorId, Pageable pageable);
}

