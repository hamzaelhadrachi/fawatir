package com.hardy.fawatir.event;

import com.hardy.fawatir.enumeration.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {

    private EventType eventType;
    private String email;

    public NewUserEvent(EventType eventType, String email) {
        super(email);
        this.eventType = eventType;
        this.email = email;
    }
}
