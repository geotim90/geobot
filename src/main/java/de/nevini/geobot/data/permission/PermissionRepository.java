package de.nevini.geobot.data.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, PermissionId> {

    Collection<Permission> findAllByServerAndChannelAndTypeAndIdAndNodeStartingWith(long server, long channel, byte type, long id, String node);

    Collection<Permission> findAllByServerAndChannelAndTypeAndIdInAndNodeStartingWith(long server, long channel, byte type, Collection<Long> ids, String node);

}
