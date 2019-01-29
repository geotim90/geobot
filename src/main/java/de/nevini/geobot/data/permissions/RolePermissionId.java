package de.nevini.geobot.data.permissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionId implements Serializable {

    private Long guild;

    private Long role;

    private String node;

}
