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
@IdClass(UserPermissionId.class)
@Entity
public class UserPermission implements Grant {

    @Id
    private Long guild;

    @Id
    private Long user;

    @Id
    private String node;

    private Byte flag;

}
