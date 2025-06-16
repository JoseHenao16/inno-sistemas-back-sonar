package com.udea.fe.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionResponseDTO {
    private Long submissionId;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Long taskId;
    private Long userId;
    private String userName;
}
