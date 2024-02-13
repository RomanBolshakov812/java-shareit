package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
//@RequestMapping(path = "/users")
@RequestMapping(path = "http://localhost:9090/users")
public class UserControllerGateway {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userClient.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Integer userId) {
        return userClient.getUser(userId);
    }
}
