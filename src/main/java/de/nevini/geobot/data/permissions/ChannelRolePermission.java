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
@IdClass(ChannelRolePermissionId.class)
@Entity
public class ChannelRolePermission implements Grant {

    @Id
    private Long guild;

    @Id
    private Long channel;

    @Id
    private Long role;

    @Id
    private String node;

    private Byte flag;

}
