package de.nevini.geobot.data.permissions;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserPermissionRepository extends CrudRepository<UserPermission, UserPermissionId> {

    Collection<UserPermission> findAllByGuildAndUserAndNodeStartingWith(Long guild, Long user, String node);

}
