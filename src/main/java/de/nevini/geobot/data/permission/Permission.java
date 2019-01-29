package de.nevini.geobot.data.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PermissionId.class)
@Entity
public class Permission {

    @Id
    private Long server;

    @Id
    private Long channel;

    @Id
    private Byte type;

    @Id
    private Long id;

    @Id
    private String node;

    private Byte flag;

}
