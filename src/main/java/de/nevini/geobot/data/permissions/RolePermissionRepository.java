package de.nevini.geobot.data.permissions;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RolePermissionRepository extends CrudRepository<RolePermission, RolePermissionId> {

    Collection<RolePermission> findAllByGuildAndRoleInAndNodeStartingWith(long idLong, Collection<Long> collect, String node);

}
