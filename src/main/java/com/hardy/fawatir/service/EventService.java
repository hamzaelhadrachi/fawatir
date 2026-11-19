package com.hardy.fawatir.service;

import com.hardy.fawatir.enumeration.EventType;
import com.hardy.fawatir.model.UserEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;

public interface EventService {

    Collection<UserEvent> getEventsByUserId(Long userId);

    void addUserEvent(String email, EventType eventType, String device, String ipAdresse);

    void addUserEvent(Long userId, EventType eventType, String device, String ipAdresse);
}
