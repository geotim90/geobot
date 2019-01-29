package de.nevini.geobot.data.permissions;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ChannelUserPermissionRepository extends CrudRepository<ChannelUserPermission, ChannelUserPermissionId> {

    Collection<ChannelUserPermission> findAllByGuildAndChannelAndUserAndNodeStartingWith(Long guild, Long channel, Long user, String node);

}
