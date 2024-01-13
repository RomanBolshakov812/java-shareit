package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDtoOut addRequest(RequestDtoIn requestDtoIn, Integer requestorId) {
        User requestor = userRepository.getUserById(requestorId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + requestorId + " не найден!"));
        requestDtoIn.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toRequest(requestDtoIn, requestor);
        return RequestMapper.toRequestDtoOut(requestRepository.save(request));
    }

    @Override
    public RequestDtoWithItems getRequest(Integer requestId, Integer sharerId) {
        User requestor = userRepository.getUserById(sharerId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + sharerId + " не найден!"));
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNullException("Запрос с id = " + requestId + " не найден!"));
        return RequestMapper.toRequestDtoWithItems(request);
    }

    @Override
    public List<RequestDtoWithItems> getRequestsByRequestorId(Integer requestorId,
                                                        Integer from, Integer size) {
        userRepository.getUserById(requestorId).orElseThrow(() ->
                new EntityNullException("Передан неверный id пользователя: id = "
                        + requestorId + "!"));
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sort);
        return RequestMapper.toPageRequestDto(requestRepository
                        .findRequestsByRequestorIdOrderByCreatedDesc(requestorId, page))
                .getContent();
    }

    @Override
    public List<RequestDtoWithItems> getRequestsOtherUsers(Integer requestorId,
                                                     Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(from, size, sort);
        return RequestMapper.toPageRequestDto(requestRepository
                .findRequestsByOtherUsers(requestorId, page)).getContent();
    }
}
