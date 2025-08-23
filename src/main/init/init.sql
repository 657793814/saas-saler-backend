CREATE DATABASE IF NOT EXISTS saas_center
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS saas_log
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

create table if not exists saas_center.tenants(
                                                  tenant_id              char(15)   not null comment 'tenant_id作为租户唯一标识，手动生成生成规则：lzd+13位字符串例：lzd3973dsfsssabc' primary key,
    tenant_name            varchar(200) default ''                not null,
    tenant_code            varchar(100) default ''                not null,
    owner_area             varchar(50)  default ''                not null,
    db_name                varchar(50)  default ''                not null,
    instance_id            varchar(100) default ''                not null,
    admin_name             varchar(50)  default ''                not null,
    admin_mail             varchar(100) default ''                not null,
    init_pwd               varchar(50)  default ''                not null,
    create_time            timestamp    default CURRENT_TIMESTAMP not null,
    update_time            timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    is_enable              tinyint(1)   default 1                 null comment '0：禁用
1：启用',
    constraint idx_db_uniq
    unique (db_name, instance_id),
    constraint idx_tenant_code
    unique (tenant_code),
    constraint idx_tenant_id
    unique (tenant_id)
    );

insert into saas_center.tenants(tenant_id, tenant_name, tenant_codem,db_name)
values ('lzd1234567890', 'liuzd', 'liuzd','saas_user1');

create table if not exists saas_center.user_to_tenant(
    openid  varchar(64) default '' not null comment '某个saas应用的用户id' primary key,
    tenant_id varchar(16) default '' not null,
    union_id varchar(16) default '' not null,
    enable  tinyint   default 0  comment '用户状态 1启用，0禁用',
    create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    index idx_openid(openid),
    constraint idx_unionid_tenantid unique (union_id, tenant_id)
    )  comment '全平台用户与租户关系映射表';

create table if not exists saas_center.users(
    union_id  varchar(64) default '' not null comment 'saas平台用户唯一标识' primary key,
    user_code varchar(64) default '' not null,
    uname varchar(64) default '' not null,
    mobile varchar(16) default '' not null comment '手机号',
    pwd varchar(128) default '' not null,
    salt varchar(32) default '' not null comment '加密盐值',
    enable  tinyint   default 0  comment '用户状态 1启用，0禁用',
    create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    index idx_unionid(union_id)
    ) comment '全平台用户表';
