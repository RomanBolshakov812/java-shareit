package ru.practicum.request;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDtoIn;
import ru.practicum.request.dto.RequestDtoOut;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService service;

    @PostMapping
    public RequestDtoOut createRequest(@RequestBody RequestDtoIn requestDtoIn,
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
            @RequestParam(value = "from") Integer from,
            @RequestParam(value = "size") Integer size) {
        return service.getRequestsByRequestorId(requestorId, from, size);
    }

    @GetMapping("/all")
    public List<RequestDtoOut> getRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestParam(value = "from") Integer from,
            @RequestParam(value = "size") Integer size) {
        return service.getRequestsOtherUsers(requestorId, from, size);
    }
}
