CREATE TABLE game
(
    id   BIGINT        NOT NULL,
    name VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE module
(
    guild  BIGINT     NOT NULL,
    module VARCHAR(4) NOT NULL,
    flag   TINYINT    NOT NULL,
    PRIMARY KEY (guild, module)
);

-- region permissions

CREATE TABLE role_permission
(
    guild BIGINT      NOT NULL,
    role  BIGINT      NOT NULL,
    node  VARCHAR(24) NOT NULL,
    flag  TINYINT     NOT NULL,
    PRIMARY KEY (guild, role, node)
);

CREATE TABLE user_permission
(
    guild BIGINT      NOT NULL,
    user  BIGINT      NOT NULL,
    node  VARCHAR(24) NOT NULL,
    flag  TINYINT     NOT NULL,
    PRIMARY KEY (guild, user, node)
);

CREATE TABLE channel_role_permission
(
    guild   BIGINT      NOT NULL,
    channel BIGINT      NOT NULL,
    role    BIGINT      NOT NULL,
    node    VARCHAR(24) NOT NULL,
    flag    TINYINT     NOT NULL,
    PRIMARY KEY (guild, channel, role, node)
);

CREATE TABLE channel_user_permission
(
    guild   BIGINT      NOT NULL,
    channel BIGINT      NOT NULL,
    user    BIGINT      NOT NULL,
    node    VARCHAR(24) NOT NULL,
    flag    TINYINT     NOT NULL,
    PRIMARY KEY (guild, channel, user, node)
);

-- endregion permissions

CREATE TABLE prefix
(
    guild  BIGINT      NOT NULL,
    prefix VARCHAR(24) NOT NULL,
    PRIMARY KEY (guild)
);
