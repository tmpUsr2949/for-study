package com.example.hogwartsartifactsonline.hogwartsuser;

import com.example.hogwartsartifactsonline.hogwartsuser.converter.UserDtoToUserConverter;
import com.example.hogwartsartifactsonline.hogwartsuser.converter.UserToUserDtoConverter;
import com.example.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import com.example.hogwartsartifactsonline.system.Result;
import com.example.hogwartsartifactsonline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    private final UserDtoToUserConverter userDtoToUserConverter; // Convert userDto to user.

    private final UserToUserDtoConverter userToUserDtoConverter; // Convert user to userDto.


    public UserController(UserService userService, UserDtoToUserConverter userDtoToUserConverter, UserToUserDtoConverter userToUserDtoConverter) {
        this.userService = userService;
        this.userDtoToUserConverter = userDtoToUserConverter;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @GetMapping
    public Result findAllUsers() {
        List<HogwartsUser> foundHogwartsUsers = this.userService.findAll();

        // Convert foundUsers to a list of UserDtos.
        List<UserDto> userDtos = foundHogwartsUsers.stream()
                .map(this.userToUserDtoConverter::convert)
                .collect(Collectors.toList());

        // Note that UserDto does not contain password field.
        return new Result(true, StatusCode.SUCCESS, "Find All Success", userDtos);
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable("userId") Integer userId) {
        HogwartsUser foundHogwartsUser = this.userService.findById(userId);
        UserDto userDto = this.userToUserDtoConverter.convert(foundHogwartsUser);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
    }

    /**
     * We are not using UserDto, but User, since we require password.
     *
     * @param newHogwartsUser
     * @return
     */
    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser newHogwartsUser) {
        HogwartsUser savedUser = this.userService.save(newHogwartsUser);
        UserDto savedUserDto = this.userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
    }

    // We are not using this to update password, need another changePassword method in this class.
    @PutMapping("/{userId}")
    public Result updateUser(
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody UserDto userDto
    ) {
        HogwartsUser update = this.userDtoToUserConverter.convert(userDto);
        HogwartsUser updatedHogwartsUser = this.userService.update(userId, update);
        UserDto updatedUserDto = this.userToUserDtoConverter.convert(updatedHogwartsUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable("userId") Integer userId) {
        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

}