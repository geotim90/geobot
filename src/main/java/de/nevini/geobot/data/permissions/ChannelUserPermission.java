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
@IdClass(ChannelUserPermissionId.class)
@Entity
public class ChannelUserPermission implements Grant {

    @Id
    private Long guild;

    @Id
    private Long channel;

    @Id
    private Long user;

    @Id
    private String node;

    private Byte flag;

}
