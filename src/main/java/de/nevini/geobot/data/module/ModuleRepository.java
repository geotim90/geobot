package de.nevini.geobot.data.module;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends CrudRepository<Module, ModuleId> {

    Optional<Module> findByServerAndModuleAndFlagGreaterThan(long server, String module, byte flag);
}
