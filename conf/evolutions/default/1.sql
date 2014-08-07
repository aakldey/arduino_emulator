# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table analog_pin (
  id                        bigint auto_increment not null,
  pin_number                integer,
  pin_value                 integer,
  generated                 tinyint(1) default 0,
  constraint pk_analog_pin primary key (id))
;

create table digital_pin (
  id                        bigint auto_increment not null,
  pin_number                integer,
  pin_state                 varchar(255),
  constraint pk_digital_pin primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table analog_pin;

drop table digital_pin;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

