package com.udea.fe.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
