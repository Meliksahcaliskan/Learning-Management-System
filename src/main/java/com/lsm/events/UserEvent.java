package com.lsm.events;

import com.lsm.model.entity.base.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class UserEvent {
    private final AppUser user;
    private final Instant timestamp = Instant.now();
}
