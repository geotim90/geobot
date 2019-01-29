package de.nevini.geobot.data.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleId implements Serializable {

    private Long server;

    private String module;

}
