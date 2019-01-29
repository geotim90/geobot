package de.nevini.geobot.services;

import de.nevini.geobot.data.module.ModuleRepository;
import de.nevini.geobot.modules.Modules;
import net.dv8tion.jda.core.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(@Autowired ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public boolean isModuleActive(Guild server, String module) {
        if (Modules.CORE.equals(module)) {
            return true;
        } else {
            return moduleRepository.findByServerAndModuleAndFlagGreaterThan(server.getIdLong(), module, (byte) 0).isPresent();
        }
    }

}
