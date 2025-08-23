# 租户库表
create database if not exists saas_user1;

create table if not exists saas_user1.t_user(
    openid  varchar(64) default '' not null comment '租户用户唯一标识' primary key,
    user_code varchar(64) default '' not null,
    uname varchar(64) default '' not null,
    mobile varchar(16) default '' not null comment '手机号',
    pwd varchar(128) default '' not null,
    salt varchar(32) default '' not null comment '加密盐值',
    enable  tinyint   default 0  comment '用户状态 1启用，0禁用',
    create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    index idx_openid(openid)
    ) comment '租户用户表';