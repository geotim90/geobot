package de.nevini.geobot.data.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionId implements Serializable {

    private Long server;

    private Long channel;

    private Byte type;

    private Long id;

    private String node;

}
