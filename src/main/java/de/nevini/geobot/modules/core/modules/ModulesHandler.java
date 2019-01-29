package de.nevini.geobot.modules.core.modules;

import de.nevini.geobot.data.module.Module;
import de.nevini.geobot.data.module.ModuleRepository;
import de.nevini.geobot.modules.Modules;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModulesHandler {

    private final ModuleRepository moduleRepository;

    public ModulesHandler(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public boolean isActive(Guild guild, String module) {
        if (Modules.CORE.equals(module)) {
            return true;
        } else {
            return !moduleRepository.findAllByGuildAndModuleAndFlagGreaterThan(guild.getIdLong(), module, (byte) 0).isEmpty();
        }
    }

    public Map<String, Boolean> list(Guild guild) {
        final Collection<Module> beans = moduleRepository.findAllByGuildAndFlagGreaterThan(guild.getIdLong(), (byte) 0);
        return Modules.list().stream().collect(Collectors.toMap(
                module -> module,
                module -> Modules.CORE.equals(module) || beans.stream().anyMatch(bean -> bean.getModule().equals(module))
        ));
    }

    public void activate(Guild guild, String module) {
        final Module bean = new Module(guild.getIdLong(), module, (byte) 1);
        log.info("Storing bean: " + bean.toString());
        moduleRepository.save(bean);
    }

    public void disable(Guild guild, String module) {
        final Module bean = new Module(guild.getIdLong(), module, (byte) -1);
        log.info("Storing bean: " + bean.toString());
        moduleRepository.save(bean);
    }

}
