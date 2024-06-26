package ru.practicum.request;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.request.dto.RequestDto;

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

    public ResponseEntity<Object> createRequest(RequestDto requestDto, Integer ownerId) {
        return post("", ownerId, requestDto);
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
