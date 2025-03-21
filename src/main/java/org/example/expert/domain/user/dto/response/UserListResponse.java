package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserListResponse {

    private final Long id;
    private final String email;
    private final String nickname;

    public UserListResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
