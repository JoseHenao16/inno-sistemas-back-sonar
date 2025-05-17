package com.udea.fe.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Long userId;
}
