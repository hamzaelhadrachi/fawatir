package com.hardy.fawatir.repository.implementation;

import com.hardy.fawatir.enumeration.EventType;
import com.hardy.fawatir.model.UserEvent;
import com.hardy.fawatir.repository.EventRepository;
import com.hardy.fawatir.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.hardy.fawatir.query.EventQuerry.INSERT_EVENT_BY_USER_EMAIL_QUERY;
import static com.hardy.fawatir.query.EventQuerry.SELECT_EVENTS_BY_USER_ID_QUERY;
import static java.util.Map.of;


@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, of("id", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAdresse) {
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, of("email", email, "type", eventType.toString(), "device", device, "ipAdresse", ipAdresse));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAdresse) {

    }
}
