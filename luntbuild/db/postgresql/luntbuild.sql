CREATE TABLE LB_ROLE(ID BIGINT NOT NULL PRIMARY KEY,NAME VARCHAR(255) NOT NULL,CONSTRAINT SYS_CT_1 UNIQUE(NAME));
CREATE TABLE LB_ROLES_MAPPING(ID BIGINT NOT NULL PRIMARY KEY,FK_USER_ID BIGINT NOT NULL,FK_PROJECT_ID BIGINT NOT NULL,FK_ROLE_ID BIGINT NOT NULL);
CREATE TABLE LB_BUILD(ID BIGINT NOT NULL PRIMARY KEY,STATUS INTEGER,VERSION VARCHAR(255),LABEL_STRATEGY INTEGER,POSTBUILD_STRATEGY INTEGER,HAVE_LABEL_ON_HEAD BOOLEAN,BUILD_TYPE INTEGER,START_DATE TIMESTAMP,END_DATE TIMESTAMP,REBUILD BOOLEAN,VCS_LIST BYTEA,BUILDER_LIST BYTEA,POSTBUILDER_LIST BYTEA,FK_SCHEDULE_ID BIGINT NOT NULL);
CREATE TABLE LB_USER(ID BIGINT NOT NULL PRIMARY KEY,NAME VARCHAR(255) NOT NULL,PASSWORD VARCHAR(255),CAN_CREATE_PROJECT BOOLEAN,CONTACTS BYTEA,FULLNAME VARCHAR(255),CONSTRAINT SYS_CT_3 UNIQUE(NAME));
CREATE TABLE LB_PROJECT(ID BIGINT NOT NULL PRIMARY KEY,NAME VARCHAR(255) NOT NULL,DESCRIPTION VARCHAR(255),NOTIFIERS BYTEA,VCS_LIST BYTEA,BUILDER_LIST BYTEA,VARIABLES VARCHAR(2048),LOG_LEVEL INTEGER,CONSTRAINT SYS_CT_5 UNIQUE(NAME));
CREATE TABLE LB_VCS_LOGIN(ID BIGINT NOT NULL PRIMARY KEY,FK_PROJECT_ID BIGINT NOT NULL,LOGIN VARCHAR(255),FK_USER_ID BIGINT NOT NULL,CONSTRAINT SYS_CT_7 UNIQUE(FK_PROJECT_ID,LOGIN));
CREATE TABLE LB_SCHEDULE(ID BIGINT NOT NULL PRIMARY KEY,FK_PROJECT_ID BIGINT NOT NULL,SCHED_DISABLED BOOLEAN, NAME VARCHAR(255) NOT NULL,DESCRIPTION VARCHAR(255),NEXT_VERSION VARCHAR(4096),NEXT_VERSION_VALUE VARCHAR(4096),VARIABLES VARCHAR(2048),QUARTZ_TRIGGER BYTEA,DEPENDENT_SCHEDULE_IDS BYTEA,BUILD_NECESSARY_CONDITION VARCHAR(255),ASSOCIATED_BUILDER_NAMES BYTEA,ASSOCIATED_POSTBUILDER_NAMES BYTEA,BUILD_TYPE INTEGER,LABEL_STRATEGY INTEGER,NOTIFY_STRATEGY INTEGER,POSTBUILD_STRATEGY INTEGER,TRIGGER_DEPENDENCY_STRATEGY INTEGER,STATUS INTEGER,STATUS_DATE TIMESTAMP,BUILD_CLEANUP_STRATEGY INTEGER,BUILD_CLEANUP_STRATEGY_DATA VARCHAR(255),WORKING_PATH VARCHAR(255),CONSTRAINT SYS_CT_9 UNIQUE(FK_PROJECT_ID,NAME));
CREATE TABLE LB_NOTIFY_MAPPING(ID BIGINT NOT NULL PRIMARY KEY,FK_PROJECT_ID BIGINT NOT NULL,FK_USER_ID BIGINT NOT NULL);
CREATE TABLE LB_PROPERTY(NAME VARCHAR(255) NOT NULL PRIMARY KEY,VALUE VARCHAR(255));
ALTER TABLE LB_ROLES_MAPPING ADD CONSTRAINT FKC01F0A03458EB40A FOREIGN KEY(FK_ROLE_ID) REFERENCES LB_ROLE(ID) ON DELETE CASCADE;
ALTER TABLE LB_VCS_LOGIN ADD CONSTRAINT FK1EF5C187EAB80C95 FOREIGN KEY(FK_USER_ID) REFERENCES LB_USER(ID) ON DELETE CASCADE;
ALTER TABLE LB_VCS_LOGIN ADD CONSTRAINT FK1EF5C187613C97DB FOREIGN KEY(FK_PROJECT_ID) REFERENCES LB_PROJECT(ID) ON DELETE CASCADE;
ALTER TABLE LB_SCHEDULE ADD CONSTRAINT FK3945EF40613C97DB FOREIGN KEY(FK_PROJECT_ID) REFERENCES LB_PROJECT(ID) ON DELETE CASCADE;
ALTER TABLE LB_NOTIFY_MAPPING ADD CONSTRAINT FK1B2007A1EAB80C95 FOREIGN KEY(FK_USER_ID) REFERENCES LB_USER(ID) ON DELETE CASCADE;
ALTER TABLE LB_NOTIFY_MAPPING ADD CONSTRAINT FK1B2007A1613C97DB FOREIGN KEY(FK_PROJECT_ID) REFERENCES LB_PROJECT(ID) ON DELETE CASCADE;
ALTER TABLE LB_ROLES_MAPPING ADD CONSTRAINT FKC01F0A03EAB80C95 FOREIGN KEY(FK_USER_ID) REFERENCES LB_USER(ID) ON DELETE CASCADE;
ALTER TABLE LB_ROLES_MAPPING ADD CONSTRAINT FKC01F0A03613C97DB FOREIGN KEY(FK_PROJECT_ID) REFERENCES LB_PROJECT(ID) ON DELETE CASCADE;
ALTER TABLE LB_BUILD ADD CONSTRAINT FK201ACA458C6B749 FOREIGN KEY(FK_SCHEDULE_ID) REFERENCES LB_SCHEDULE(ID) ON DELETE CASCADE;
INSERT INTO LB_ROLE VALUES(1,'ROLE_AUTHENTICATED');
INSERT INTO LB_ROLE VALUES(2,'ROLE_SITE_ADMIN');
INSERT INTO LB_ROLE VALUES(6,'ROLE_ANONYMOUS');
INSERT INTO LB_ROLE VALUES(3,'LUNTBUILD_PRJ_ADMIN');
INSERT INTO LB_ROLE VALUES(4,'LUNTBUILD_PRJ_BUILDER');
INSERT INTO LB_ROLE VALUES(5,'LUNTBUILD_PRJ_VIEWER');
INSERT INTO LB_USER VALUES(1,'<users who checked in code recently>',NULL,FALSE,NULL,NULL);
INSERT INTO LB_USER VALUES(2,'<users who checked in since last successful build>',NULL,FALSE,NULL,NULL);
