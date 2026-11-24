package com.hardy.fawatir.service.implementation;

import com.hardy.fawatir.enumeration.EventType;
import com.hardy.fawatir.model.UserEvent;
import com.hardy.fawatir.repository.EventRepository;
import com.hardy.fawatir.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return eventRepository.getEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAdresse) {
        eventRepository.addUserEvent(email, eventType, device, ipAdresse);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAdresse) {
        eventRepository.addUserEvent(userId, eventType, device, ipAdresse);
    }
}
