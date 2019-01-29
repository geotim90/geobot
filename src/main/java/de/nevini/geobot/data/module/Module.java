package de.nevini.geobot.data.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ModuleId.class)
@Entity
public class Module {

    @Id
    private Long server;

    @Id
    private String module;

    private Byte flag;

}
