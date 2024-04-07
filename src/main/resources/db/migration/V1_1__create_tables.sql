CREATE TABLE IF NOT EXISTS logs
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    cred_id          VARCHAR(255),
    target_scope     VARCHAR(255),
    success          TINYINT(1),
    access_config_id VARCHAR(255),
    tenant           VARCHAR(255),
    correlation_id   VARCHAR(255),
    operation        VARCHAR(255),
    scope            VARCHAR(255),
    scope_id         VARCHAR(255),
    failure_reason   VARCHAR(255),
    login_data       JSON,
    created_on       DATETIME,
    last_updated_on  DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS credentials
(
    client_id       VARCHAR(255) PRIMARY KEY,
    client_secret   VARCHAR(255),
    expiry          DATETIME,
    last_rotated_on DATETIME,
    scope_id        VARCHAR(255),
    status          VARCHAR(255),
    context_data    JSON,
    created_on      DATETIME,
    last_updated_on DATETIME
);

CREATE TABLE IF NOT EXISTS access_config
(
    id              VARCHAR(255) PRIMARY KEY,
    cred_id         VARCHAR(255),
    scope_id        VARCHAR(255),
    access_mode     VARCHAR(255),
    access_api_list TEXT,
    status          VARCHAR(255),
    created_on      DATETIME,
    last_updated_on DATETIME
);

CREATE TABLE IF NOT EXISTS scope
(
    id              VARCHAR(255) PRIMARY KEY,
    type            VARCHAR(255),
    identifier      VARCHAR(255),
    created_on      DATETIME,
    last_updated_on DATETIME
);

CREATE TABLE IF NOT EXISTS api_tag_config
(
    id               VARCHAR(255) NOT NULL PRIMARY KEY,
    tag              VARCHAR(255),
    method           VARCHAR(255),
    path_regex       VARCHAR(255),
    service_scope_id VARCHAR(255),
    created_on       DATETIME,
    last_updated_on  DATETIME
);