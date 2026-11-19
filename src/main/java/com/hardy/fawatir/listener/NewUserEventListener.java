package com.hardy.fawatir.listener;

import com.hardy.fawatir.event.NewUserEvent;
import com.hardy.fawatir.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        log.info("NewUserEventListener ====== / onNewUserEvent \\ =============");
        eventService.addUserEvent(event.getEmail(), event.getEventType(), "device", "PI Address");
    }


}
