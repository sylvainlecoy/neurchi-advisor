package com.neurchi.advisor.subscription.domain.model.collaborator;

import com.neurchi.advisor.subscription.domain.model.group.GroupId;

public interface CollaboratorService {

    Moderator moderatorFrom(GroupId groupId, String identity);

    Administrator administratorFrom(GroupId groupId, String identity);

    Subscriber subscriberFrom(GroupId groupId, String identity);

}
