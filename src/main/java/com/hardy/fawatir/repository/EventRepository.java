package com.hardy.fawatir.repository;

import com.hardy.fawatir.enumeration.EventType;
import com.hardy.fawatir.model.UserEvent;

import java.util.Collection;

public interface EventRepository {


    Collection<UserEvent> getEventsByUserId(Long userId);

    void addUserEvent(String email, EventType eventType, String device, String ipAdresse);

    void addUserEvent(Long userId, EventType eventType, String device, String ipAdresse);
}
