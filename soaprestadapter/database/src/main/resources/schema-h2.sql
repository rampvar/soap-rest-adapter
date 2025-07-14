CREATE TABLE IF NOT EXISTS tbl_generated_wsdl_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wsdl_url VARCHAR(1000) NOT NULL,
    class_data BLOB NOT NULL,
    generated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_user (
    user_id BIGINT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_role (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_user_group (
    user_group_id BIGINT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL,
    is_authorized BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tbl_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tbl_role(role_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tbl_role_group (
    role_id BIGINT NOT NULL,
    user_group_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_group_id),
    FOREIGN KEY (role_id) REFERENCES tbl_role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (user_group_id) REFERENCES tbl_user_group(user_group_id) ON DELETE CASCADE
);