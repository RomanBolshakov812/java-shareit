package ru.practicum.shareit.request;

import java.util.List;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

public interface RequestService {

    RequestDtoOut addRequest(RequestDtoIn requestDtoIn, Integer requestorId);

    RequestDtoOut getRequest(Integer requestId, Integer sharerId);

    List<RequestDtoOut> getRequestsByRequestorId(Integer requestorId,
                                                        Integer from, Integer size);

    List<RequestDtoOut> getRequestsOtherUsers(Integer requestorId,
                                                     Integer from, Integer size);
}
