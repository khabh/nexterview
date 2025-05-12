package com.nexterview.server.repository;

import com.nexterview.server.domain.Interview;
import com.nexterview.server.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByUser(User user);
}
