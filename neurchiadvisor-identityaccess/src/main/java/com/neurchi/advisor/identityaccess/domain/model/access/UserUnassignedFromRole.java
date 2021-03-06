package com.neurchi.advisor.identityaccess.domain.model.access;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.time.Instant;

public final class UserUnassignedFromRole implements DomainEvent {

    private final TenantId tenantId;
    private final String roleName;
    private final String username;
    private final Instant occurredOn;
    private final int eventVersion;

    UserUnassignedFromRole(final TenantId tenantId, final String roleName, final String username) {
        this.tenantId = tenantId;
        this.roleName = roleName;
        this.username = username;
        this.occurredOn = Instant.now();
        this.eventVersion = 1;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String roleName() {
        return roleName;
    }

    public String groupName() {
        return username;
    }

    @Override
    public int eventVersion() {
        return eventVersion;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
