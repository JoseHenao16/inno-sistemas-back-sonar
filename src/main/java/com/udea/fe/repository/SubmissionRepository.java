package com.udea.fe.repository;

import com.udea.fe.entity.Submission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
  List<Submission> findByTask_TaskId(Long taskId);
  List<Submission> findByTask_TaskIdAndUser_UserId(Long taskId, Long userId);
}
