package de.nevini.geobot.data.module;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ModuleRepository extends CrudRepository<Module, ModuleId> {

    Collection<Module> findAllByGuildAndFlagGreaterThan(Long guild, Byte flag);

    Collection<Module> findAllByGuildAndModuleAndFlagGreaterThan(Long guild, String module, Byte flag);

}
