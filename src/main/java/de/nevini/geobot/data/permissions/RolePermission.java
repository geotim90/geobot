package de.nevini.geobot.data.permissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(RolePermissionId.class)
@Entity
public class RolePermission implements Grant {

    @Id
    private Long guild;

    @Id
    private Long role;

    @Id
    private String node;

    private Byte flag;

}
