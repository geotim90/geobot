package de.nevini.geobot.modules.core.permissions;

import de.nevini.geobot.data.permissions.*;
import de.nevini.geobot.messaging.MessageContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PermissionsHandler {

    private final ChannelUserPermissionRepository channelUserPermissionRepository;
    private final ChannelRolePermissionRepository channelRolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public PermissionsHandler(
            @Autowired @NonNull ChannelUserPermissionRepository channelUserPermissionRepository,
            @Autowired @NonNull ChannelRolePermissionRepository channelRolePermissionRepository,
            @Autowired @NonNull UserPermissionRepository userPermissionRepository,
            @Autowired @NonNull RolePermissionRepository rolePermissionRepository
    ) {
        this.channelUserPermissionRepository = channelUserPermissionRepository;
        this.channelRolePermissionRepository = channelRolePermissionRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    /**
     * Returns {@code true} if the current user has been granted any child node of the given permission node in the current channel.
     */
    public boolean hasPermission(MessageContext context, String node) {
        if (context.getMessage().getChannelType() == ChannelType.PRIVATE) {
            return true; // no guild context
        }

        return hasPermission(context.getMessage().getTextChannel(), context.getMessage().getMember(), node);
    }

    /**
     * Returns {@code true} if the provided member has been granted any child node of the provided permission node in the provided channel.
     */
    public boolean hasPermission(Channel channel, Member member, String node) {
        if (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR)) {
            return true; // no restrictions
        }

        final Optional<Boolean> channelUserPermission = getChannelUserPermission(channel, member, node);
        if (channelUserPermission.isPresent()) {
            return channelUserPermission.get();
        }

        final Optional<Boolean> channelRolePermission = getChannelRolePermission(channel, member, node);
        if (channelRolePermission.isPresent()) {
            return channelRolePermission.get();
        }

        final Optional<Boolean> userPermission = getUserPermission(member, node);
        if (userPermission.isPresent()) {
            return userPermission.get();
        }

        final Optional<Boolean> rolePermission = getRolePermission(member, node);
        return rolePermission.orElse(false); // deny by default
    }


    private Optional<Boolean> getChannelUserPermission(Channel channel, Member member, String node) {
        return resolveGrants(channelUserPermissionRepository.findAllByGuildAndChannelAndUserAndNodeStartingWith(
                member.getGuild().getIdLong(),
                channel.getIdLong(),
                member.getUser().getIdLong(),
                node
        ));
    }

    private Optional<Boolean> getChannelRolePermission(Channel channel, Member member, String node) {
        final Map<Long, Role> roles = getMemberRoles(member);
        return resolveGrants(channelRolePermissionRepository.findAllByGuildAndChannelAndRoleInAndNodeStartingWith(
                member.getGuild().getIdLong(),
                channel.getIdLong(),
                roles.values().stream().map(ISnowflake::getIdLong).collect(Collectors.toList()),
                node
        ));
    }

    private Optional<Boolean> getUserPermission(Member member, String node) {
        return resolveGrants(userPermissionRepository.findAllByGuildAndUserAndNodeStartingWith(
                member.getGuild().getIdLong(),
                member.getUser().getIdLong(),
                node
        ));
    }

    private Optional<Boolean> getRolePermission(Member member, String node) {
        final Map<Long, Role> roles = getMemberRoles(member);
        return resolveGrants(rolePermissionRepository.findAllByGuildAndRoleInAndNodeStartingWith(
                member.getGuild().getIdLong(),
                roles.values().stream().map(ISnowflake::getIdLong).collect(Collectors.toList()),
                node
        ));
    }

    private Map<Long, Role> getMemberRoles(Member user) {
        return ListUtils.union(user.getRoles(), Collections.singletonList(user.getGuild().getPublicRole()))
                .stream().collect(Collectors.toMap(Role::getIdLong, r -> r));
    }

    private Optional<Boolean> resolveGrants(Collection<? extends Grant> grants) {
        boolean anyAllow = false;
        boolean anyDeny = false;
        for (Grant grant : grants) {
            if (grant.getFlag() != 0) {
                if (grant.getFlag() > 0) {
                    anyAllow = true;
                } else {
                    anyDeny = true;
                }
            }
        }
        if (anyAllow) {
            return Optional.of(true);
        } else if (anyDeny) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

}
