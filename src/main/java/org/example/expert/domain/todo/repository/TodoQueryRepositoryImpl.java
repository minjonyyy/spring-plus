package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;


@Repository
public class TodoQueryRepositoryImpl implements TodoQueryRepository{
    private final JPAQueryFactory jpaQueryFactory;


    public TodoQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId){

        Todo result = jpaQueryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);

    }

    @Override
    public Page<TodoSearchResponse> searchTodo(Pageable pageable, String title, String nickname, LocalDateTime startDate, LocalDateTime endDate) {
        // 페이지를 위한 객체와 이렇게 많아지면 검색을 위한 객체를 분리할 수도 있음
        // todo 검색엔진 객체 분리 해보기

        List<TodoSearchResponse> result = jpaQueryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.id,
                        todo.title,
                        Projections.constructor(UserResponse.class, todo.user.id, todo.user.nickname),
                        todo.numOfManagers,
                        todo.numOfComments,
                        todo.createdAt
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(containsTitle(title),
                        containsNickname(nickname),
                        betweenDate(startDate, endDate)
                )
                .groupBy(todo.id, user.id, user.nickname)
                .distinct()
                .orderBy(todo.createdAt.desc()) // 생성일 기준 내림차순
                .offset(pageable.getOffset()) // 페이징
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(containsTitle(title),
                        containsNickname(nickname),
                        betweenDate(startDate, endDate)
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    private BooleanExpression containsTitle(String title) {
        if(StringUtils.isNullOrEmpty(title)){
            return null;
        }
        return todo.title.containsIgnoreCase(title);
    }

    private BooleanExpression containsNickname(String nickname) {
        if(StringUtils.isNullOrEmpty(nickname)){
            return null;
        }
        return user.nickname.containsIgnoreCase(nickname);
    }

    private BooleanExpression betweenDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return todo.createdAt.between(startDate, endDate);
        } else if (startDate != null) {
            return todo.createdAt.goe(startDate);
        } else if (endDate != null) {
            return todo.createdAt.loe(endDate);
        }
        return null;
    }

}
