package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    Page<Request> findRequestsByRequestorIdOrderByCreatedDesc(Integer requestorId,
                                                              Pageable pageable);

    @Query(nativeQuery = true, value = "select * from requests r "
            + "where r.id <> ?1 order by r.created desc")
    Page<Request> findRequestsByOtherUsers(Integer requestorId, Pageable pageable);
}

