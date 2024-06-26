package ru.practicum.request.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.request.dto.RequestDtoIn;
import ru.practicum.request.dto.RequestDtoOut;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

public class RequestMapper {

    public static Request toRequest(RequestDtoIn requestDtoIn, User requestor) {
        Request request = new Request();
        request.setId(requestDtoIn.getId());
        request.setDescription(requestDtoIn.getDescription());
        request.setRequestor(requestor);
        request.setCreated(requestDtoIn.getCreated());
        return request;
    }

    public static RequestDtoIn toRequestDto(Request request) {
        return new RequestDtoIn(
                request.getId(),
                request.getDescription(),
                request.getCreated()
        );
    }

    public static RequestDtoOut toRequestDtoOut(Request request) {
        return new RequestDtoOut(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                ItemMapper.toListItemDto(request.getItems())
        );
    }

    public static List<RequestDtoOut> toListRequestDtoOut(List<Request> requests) {
        List<RequestDtoOut> requestsDto = new ArrayList<>();
        for (Request request : requests) {
            requestsDto.add(toRequestDtoOut(request));
        }
        return requestsDto;
    }

    public static Page<RequestDtoOut> toPageRequestDto(Page<Request> requests) {
        return requests.map(RequestMapper::toRequestDtoOut);
    }
}
