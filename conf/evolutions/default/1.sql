# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table analog_pin (
  id                        bigint auto_increment not null,
  pin_number                integer,
  pin_value                 integer,
  constraint pk_analog_pin primary key (id))
;

create table digital_pin (
  id                        bigint auto_increment not null,
  pin_number                integer,
  pin_state                 varchar(255),
  constraint pk_digital_pin primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table analog_pin;

drop table digital_pin;

SET FOREIGN_KEY_CHECKS=1;

