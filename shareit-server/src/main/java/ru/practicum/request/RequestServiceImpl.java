package ru.practicum.request;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityNullException;
import ru.practicum.request.dto.RequestDtoIn;
import ru.practicum.request.dto.RequestDtoOut;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDtoOut addRequest(RequestDtoIn requestDtoIn, Integer requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + requestorId + " не найден!"));
        requestDtoIn.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toRequest(requestDtoIn, requestor);
        requestRepository.save(request);
        return RequestMapper.toRequestDtoOut(request);
    }

    @Override
    public RequestDtoOut getRequest(Integer requestId, Integer sharerId) {
        User requestor = userRepository.findById(sharerId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + sharerId + " не найден!"));
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNullException("Запрос с id = " + requestId + " не найден!"));
        return RequestMapper.toRequestDtoOut(request);
    }

    @Override
    public List<RequestDtoOut> getRequestsByRequestorId(Integer requestorId,
                                                        Integer from, Integer size) {
        userRepository.findById(requestorId).orElseThrow(() ->
                new EntityNullException("Передан неверный id пользователя: id = "
                        + requestorId + "!"));
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sort);
        Page<Request> requestsPage = requestRepository
                .findRequestsByRequestorIdOrderByCreatedDesc(requestorId, page);
        return RequestMapper.toPageRequestDto(requestsPage).getContent();
    }

    @Override
    public List<RequestDtoOut> getRequestsOtherUsers(Integer requestorId,
                                                     Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sort);
        return RequestMapper.toPageRequestDto(requestRepository
                .findRequestsByOtherUsers(requestorId, page)).getContent();
    }
}
