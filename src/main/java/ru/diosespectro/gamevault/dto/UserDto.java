package ru.diosespectro.gamevault.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.diosespectro.gamevault.entity.Role;
import ru.diosespectro.gamevault.entity.User;
import ru.diosespectro.gamevault.repository.RoleRepository;
import ru.diosespectro.gamevault.repository.UserRepository;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotEmpty(message = "Поле Имя пользователя не должно быть пустым")
    private String username;

    @NotEmpty(message = "Поле Email не должно быть пустым")
    @Email
    private String email;

    @NotEmpty(message = "Поле Пароль не должно быть пустым")
    private String password;

    private String role;
}