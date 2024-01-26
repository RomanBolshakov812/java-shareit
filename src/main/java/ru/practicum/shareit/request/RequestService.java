package ru.practicum.shareit.request;

import java.util.List;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

public interface RequestService {

    public RequestDtoOut addRequest(RequestDtoIn requestDtoIn, Integer requestorId);

    public RequestDtoOut getRequest(Integer requestId, Integer sharerId);

    public List<RequestDtoOut> getRequestsByRequestorId(Integer requestorId,
                                                        Integer from, Integer size);

    public List<RequestDtoOut> getRequestsOtherUsers(Integer requestorId,
                                                     Integer from, Integer size);
}
