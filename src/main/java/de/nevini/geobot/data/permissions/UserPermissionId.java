package de.nevini.geobot.data.permissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionId implements Serializable {

    private Long guild;

    private Long user;

    private String node;

}
