package com.udea.fe.DTO;

import lombok.Data;

@Data
public class SubmissionRequestDTO {
    private String content;
    private String fileUrl;
    private Long taskId;
    private Long userId;
}
