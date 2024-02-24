package ru.practicum.request;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestControllerGateway {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Integer requestorId) {
        return requestClient.createRequest(requestDto, requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Integer requestId,
                                    @RequestHeader("X-Sharer-User-Id") Integer sharerId) {
        return requestClient.getRequest(requestId, sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestorId(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return requestClient.getRequestsByRequestorId(requestorId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size) {
        return requestClient.getRequestsByOtherUsers(requestorId, from, size);
    }
}
