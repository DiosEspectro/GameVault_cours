package ru.diosespectro.gamevault.service;

import ru.diosespectro.gamevault.dto.UserDto;
import ru.diosespectro.gamevault.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByUsername(String username);

    List<UserDto> findAllUsers();

    void addRoleToUser(Long userId, Long roleId);

    Long getCurUserId();

    String getCurUserRole();
}
