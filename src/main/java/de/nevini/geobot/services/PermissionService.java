package de.nevini.geobot.services;

import de.nevini.geobot.data.permission.Permission;
import de.nevini.geobot.data.permission.PermissionRepository;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final static byte TYPE_SERVER = 1;
    private final static byte TYPE_ROLE = 2;
    private final static byte TYPE_USER = 3;
    private final static byte TYPE_CHANNEL = 4;
    private final static byte TYPE_CHANNEL_ROLE = 5;
    private final static byte TYPE_CHANNEL_USER = 6;

    private final PermissionRepository permissionRepository;

    public PermissionService(@Autowired PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean hasPermission(TextChannel channel, Member user, String node) {
        if (user.isOwner() || user.hasPermission(net.dv8tion.jda.core.Permission.ADMINISTRATOR)) {
            return true; // no restrictions
        }

        final Optional<Boolean> channelUserPermission = getChannelUserPermission(channel, user, node);
        if (channelUserPermission.isPresent()) {
            return channelUserPermission.get();
        }

        final Optional<Boolean> channelRolePermission = getChannelRolePermission(channel, user, node);
        if (channelRolePermission.isPresent()) {
            return channelRolePermission.get();
        }

        final Optional<Boolean> channelPermission = getChannelPermission(channel, node);
        if (channelPermission.isPresent()) {
            return channelPermission.get();
        }

        final Optional<Boolean> userPermission = getUserPermission(user, node);
        if (userPermission.isPresent()) {
            return userPermission.get();
        }

        final Optional<Boolean> rolePermission = getRolePermission(user, node);
        if (rolePermission.isPresent()) {
            return rolePermission.get();
        }

        final Optional<Boolean> serverPermission = getServerPermission(channel.getGuild(), node);
        return serverPermission.orElse(false); // deny by default
    }

    private Optional<Boolean> getChannelUserPermission(TextChannel channel, Member user, String node) {
        return resolvePermissions(permissionRepository.findAllByServerAndChannelAndTypeAndIdAndNodeStartingWith(
                channel.getGuild().getIdLong(),
                channel.getIdLong(),
                TYPE_CHANNEL_USER,
                user.getUser().getIdLong(),
                node
        ));
    }

    private Optional<Boolean> getChannelRolePermission(TextChannel channel, Member user, String node) {
        return resolvePermissions(permissionRepository.findAllByServerAndChannelAndTypeAndIdInAndNodeStartingWith(
                channel.getGuild().getIdLong(),
                channel.getIdLong(),
                TYPE_CHANNEL_ROLE,
                user.getRoles().stream().map(ISnowflake::getIdLong).collect(Collectors.toList()),
                node
        ));
    }

    private Optional<Boolean> getChannelPermission(TextChannel channel, String node) {
        return resolvePermissions(permissionRepository.findAllByServerAndChannelAndTypeAndIdAndNodeStartingWith(
                channel.getGuild().getIdLong(),
                channel.getIdLong(),
                TYPE_CHANNEL,
                channel.getGuild().getIdLong(),
                node
        ));
    }

    private Optional<Boolean> getUserPermission(Member user, String node) {
        return resolvePermissions(permissionRepository.findAllByServerAndChannelAndTypeAndIdAndNodeStartingWith(
                user.getGuild().getIdLong(),
                user.getGuild().getIdLong(),
                TYPE_USER,
                user.getUser().getIdLong(),
                node
        ));
    }

    private Optional<Boolean> getRolePermission(Member user, String node) {
        return resolvePermissions(permissionRepository.findAllByServerAndChannelAndTypeAndIdInAndNodeStartingWith(
                user.getGuild().getIdLong(),
                user.getGuild().getIdLong(),
                TYPE_ROLE,
                user.getRoles().stream().map(ISnowflake::getIdLong).collect(Collectors.toList()),
                node
        ));
    }

    private Optional<Boolean> getServerPermission(Guild server, String node) {
        return resolvePermissions(permissionRepository.findAllByServerAndChannelAndTypeAndIdAndNodeStartingWith(
                server.getIdLong(),
                server.getIdLong(),
                TYPE_SERVER,
                server.getIdLong(),
                node
        ));
    }

    private Optional<Boolean> resolvePermissions(Collection<Permission> permissions) {
        boolean anyAllow = false;
        boolean anyDeny = false;
        for (Permission permission : permissions) {
            if (permission.getFlag() != 0) {
                if (permission.getFlag() > 0) {
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
