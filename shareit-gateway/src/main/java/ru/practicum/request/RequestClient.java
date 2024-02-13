package ru.practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDtoIn;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl
                                + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(RequestDtoIn requestDtoIn, Integer ownerId) {
        return post("", ownerId, requestDtoIn);
    }

    public ResponseEntity<Object> getRequest(Integer requestId, Integer sharerId) {
        return get("/" + requestId, sharerId);
    }

    public ResponseEntity<Object> getRequestsByRequestorId(Integer requestorId, Integer from,
                                                        Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", requestorId, parameters);
    }

    public ResponseEntity<Object> getRequestsByOtherUsers(Integer requestorId, Integer from,
                                                           Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", requestorId, parameters);
    }
}
