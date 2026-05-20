USE datacatstore;

CREATE TABLE IF NOT EXISTS accounts (
    UserID char(36) NOT NULL,
    eMail varchar(150) NOT NULL,
    Username varchar(20) NOT NULL,
    PasswordHashed text NOT NULL,
    Created BIGINT NOT NULL DEFAULT UNIX_TIMESTAMP(),
    LastLogin BIGINT NOT NULL DEFAULT UNIX_TIMESTAMP(),
    AccountStatus char(36) NOT NULL,
    VerificationStatus char(36) NOT NULL,
    PRIMARY KEY (UserID, eMail, Username)
);

CREATE TABLE IF NOT EXISTS accounts_name_history (
    UserID char(36) NOT NULL,
    ChangeDate BIGINT NOT NULL DEFAULT UNIX_TIMESTAMP(),
    OldName varchar(20) NOT NULL,
    NewName varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS yarn_meta (
    YarnID char(36) NOT NULL PRIMARY KEY,
    YarnName varchar(50) NOT NULL,
    AuthorID char(36) NOT NULL,
    Created BIGINT NOT NULL DEFAULT UNIX_TIMESTAMP(),
    Status char(36) NOT NULL,
    LongDescription nvarchar(5000) NOT NULL,
    ShortDescription nvarchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS yarn_meta_tags_assigned (
    YarnID char(36) NOT NULL,
    TagID char(36) NOT NULL,
    DisplayPriority INT NOT NULL DEFAULT 0,
    INDEX (YarnID, TagID)
);
CREATE TABLE IF NOT EXISTS yarn_meta_tags (
    TagID char(36) NOT NULL PRIMARY KEY,
    TagName varchar(30) NOT NULL,
    TagColor char(15) NOT NULL DEFAULT '000;000;000;100'
)