package com.cinema.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Long id;
    private Long userId;
    private String email;
    private String subject;
    private String content;
    private LocalDateTime sentAt;
    private Boolean success;
}