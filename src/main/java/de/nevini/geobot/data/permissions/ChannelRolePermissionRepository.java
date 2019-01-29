package de.nevini.geobot.data.permissions;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ChannelRolePermissionRepository extends CrudRepository<ChannelRolePermission, ChannelRolePermissionId> {

    Collection<ChannelRolePermission> findAllByGuildAndChannelAndRoleInAndNodeStartingWith(Long guild, Long channel, Collection<Long> role, String node);

}
