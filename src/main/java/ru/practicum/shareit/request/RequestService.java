package ru.practicum.shareit.request;

import java.util.List;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;

public interface RequestService {

    public RequestDtoOut addRequest(RequestDtoIn requestDtoIn, Integer requestorId);

    public RequestDtoWithItems getRequest(Integer requestId, Integer sharerId);

    public List<RequestDtoWithItems> getRequestsByRequestorId(Integer requestorId,
                                                        Integer from, Integer size);

    public List<RequestDtoWithItems> getRequestsOtherUsers(Integer requestorId,
                                                     Integer from, Integer size);
}
