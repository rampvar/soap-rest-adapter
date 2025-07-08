CREATE TABLE IF NOT EXISTS tbl_generated_wsdl_classes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    wsdl_url TEXT NOT NULL,
    class_data BLOB NOT NULL,
    generated_at TEXT NOT NULL
);

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS tbl_user (
    user_id INTEGER PRIMARY KEY,
    user_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_role (
    role_id INTEGER PRIMARY KEY,
    role_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_user_group (
    user_group_id INTEGER PRIMARY KEY,
    role_name TEXT NOT NULL,
    is_authorized INTEGER NOT NULL CHECK (is_authorized IN (0, 1))
);

CREATE TABLE IF NOT EXISTS tbl_user_role (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tbl_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tbl_role(role_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tbl_role_group (
    role_id INTEGER NOT NULL,
    user_group_id INTEGER NOT NULL,
    PRIMARY KEY (role_id, user_group_id),
    FOREIGN KEY (role_id) REFERENCES tbl_role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (user_group_id) REFERENCES tbl_user_group(user_group_id) ON DELETE CASCADE
);