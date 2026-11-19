package com.hardy.fawatir.service.implementation;

import com.hardy.fawatir.enumeration.EventType;
import com.hardy.fawatir.model.UserEvent;
import com.hardy.fawatir.service.EventService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAdresse) {

    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAdresse) {

    }
}
