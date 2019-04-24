package de.nevini.geobot.services;

import de.nevini.geobot.data.module.Module;
import de.nevini.geobot.data.module.ModuleRepository;
import de.nevini.geobot.modules.Modules;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(@Autowired ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Map<String, Boolean> getAllModuleActivity(Guild server) {
        final Map<String, Boolean> result = new LinkedHashMap<>();
        Modules.list().forEach(
                module -> result.put(module, Modules.CORE.equals(module))
        );
        moduleRepository.findAllByServerAndFlagGreaterThan(server.getIdLong(), (byte) 0).forEach(
                module -> result.put(module.getModule(), true)
        );
        return result;
    }

    public boolean isModuleActive(Guild server, String module) {
        if (Modules.CORE.equals(module)) {
            return true;
        } else {
            return moduleRepository.findByServerAndModuleAndFlagGreaterThan(server.getIdLong(), module, (byte) 0).isPresent();
        }
    }

    public void setModuleActive(Guild server, String module, boolean active) {
        final Module data = new Module(server.getIdLong(), module, active ? (byte) 1 : (byte) -1);
        log.info("Storing data: {}", data);
        moduleRepository.save(data);
    }

}
