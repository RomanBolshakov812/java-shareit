package ru.practicum.shareit.request;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService service;

    @PostMapping
    public RequestDtoOut createRequest(@Valid @RequestBody RequestDtoIn requestDtoIn,
                                      @RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        return service.addRequest(requestDtoIn, requestorId);
    }

    @GetMapping("/{requestId}")
    public RequestDtoOut getRequest(@PathVariable Integer requestId,
                           @RequestHeader("X-Sharer-User-Id") Integer sharerId) {
        return service.getRequest(requestId, sharerId);
    }

    @GetMapping
    public List<RequestDtoOut> getRequestByRequestorId(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return service.getRequestsByRequestorId(requestorId, from, size);
    }

    @GetMapping("/all")
    public List<RequestDtoOut> getRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return service.getRequestsOtherUsers(requestorId, from, size);
    }
}
