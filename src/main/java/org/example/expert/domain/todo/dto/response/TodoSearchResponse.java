package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
public class TodoSearchResponse {
    private Long id;
    private String title;
    private UserResponse user;
    private Integer numOfManagers;
    private Integer numOfComments;
    private LocalDateTime createdAt;


    public TodoSearchResponse(Long id, String title, UserResponse user, Integer numOfManagers, Integer numOfComments, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.user = user;
        this.numOfManagers = numOfManagers;
        this.numOfComments = numOfComments;
        this.createdAt = createdAt;
    }

    public TodoSearchResponse() {}
}
