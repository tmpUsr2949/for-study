package com.example.hogwartsartifactsonline.security;

import com.example.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import com.example.hogwartsartifactsonline.hogwartsuser.MyUserPrincipal;
import com.example.hogwartsartifactsonline.hogwartsuser.converter.UserToUserDtoConverter;
import com.example.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user info.
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDto userDto = userToUserDtoConverter.convert(hogwartsUser);

        // Create a JWT.
        String token = jwtProvider.createToken(authentication);

        HashMap<String, Object> loginResultMap = new HashMap<>();

        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
