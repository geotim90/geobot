package de.nevini.geobot.data.permissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelUserPermissionId implements Serializable {

    private Long guild;

    private Long channel;

    private Long user;

    private String node;

}
