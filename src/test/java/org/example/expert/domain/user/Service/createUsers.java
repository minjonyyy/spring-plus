package org.example.expert.domain.user.Service;

import jakarta.transaction.Transactional;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class createUsers {

    private static final int TOTAL_USERS = 1_000_000;

    @Autowired
    private UserJdbcRepository userRepository;

    @Test
    @Transactional
    @Rollback(false)
    void 대용량_유저_생성(){

        List<User> users = new ArrayList<>();

        for (int i = 0; i <TOTAL_USERS ; i++) {

            UserRole userRole = i % 2== 0? UserRole.ROLE_USER : UserRole.ROLE_ADMIN;

            users.add(new User( "email" + i + "@example.com", "password" + i, userRole, "nickname" + i));

//            try{
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            // 1000개씩 저장 (Batch Insert)
            if (users.size() % 1000 == 0) {
                userRepository.saveAll(users);
                users.clear(); // 저장 후 리스트 초기화
            }

        }

        if (!users.isEmpty()) {
            userRepository.saveAll(users);
        }

    }
}
