INSERT INTO `mydatabase`.`business_process` (`name`) VALUES ('business_process_1');
INSERT INTO `mydatabase`.`business_process` (`name`) VALUES ('business_process_2');
INSERT INTO `mydatabase`.`business_process` (`name`) VALUES ('business_process_3');

INSERT INTO `mydatabase`.`cde_type` (`name`) VALUES ('type_1');
INSERT INTO `mydatabase`.`cde_type` (`name`) VALUES ('type_2');
INSERT INTO `mydatabase`.`cde_type` (`name`) VALUES ('type_3');

INSERT INTO `mydatabase`.`confidential_level` (`name`) VALUES ('level_1');
INSERT INTO `mydatabase`.`confidential_level` (`name`) VALUES ('level_2');
INSERT INTO `mydatabase`.`confidential_level` (`name`) VALUES ('level_3');

INSERT INTO `mydatabase`.`data_category` (`name`) VALUES ('category_1');
INSERT INTO `mydatabase`.`data_category` (`name`) VALUES ('category_2');
INSERT INTO `mydatabase`.`data_category` (`name`) VALUES ('category_3');

INSERT INTO `mydatabase`.`data_group` (`name`) VALUES ('group_1');
INSERT INTO `mydatabase`.`data_group` (`name`) VALUES ('group_2');
INSERT INTO `mydatabase`.`data_group` (`name`) VALUES ('group_3');

INSERT INTO `mydatabase`.`db` (`name`) VALUES ('databse_1');
INSERT INTO `mydatabase`.`db` (`name`) VALUES ('databse_2');
INSERT INTO `mydatabase`.`db` (`name`) VALUES ('databse_3');

INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('1', 'VT', 'Viettel', '0');
INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('2', 'VS', 'Vietel Solution', '1');
INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('3', 'VD', 'Viettel Digital', '1');
INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('4', 'VSD', 'Viettel Giải pháp số', '2');
INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('5', 'VSDH', 'Viettel Giải pháp y tế số', '2');
INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('6', 'VDM', 'Viettel Money', '3');
INSERT INTO `mydatabase`.`department` (`id`, `code`, `name`, `parent`) VALUES ('7', 'VDB', 'Viettel Bank', '3');

INSERT INTO `mydatabase`.`it_system` (`code`, `name`) VALUES ('sys1', 'system_1');
INSERT INTO `mydatabase`.`it_system` (`code`, `name`) VALUES ('sys2', 'system_2');
INSERT INTO `mydatabase`.`it_system` (`code`, `name`) VALUES ('sys3', 'system_3');

INSERT INTO `mydatabase`.`report` (`name`) VALUES ('report_1');
INSERT INTO `mydatabase`.`report` (`name`) VALUES ('report_2');
INSERT INTO `mydatabase`.`report` (`name`) VALUES ('report_3');

INSERT INTO `mydatabase`.`staff` (`code`, `name`, `username`) VALUES ('Staff1', 'Nhân viên 1', 'staff1');
INSERT INTO `mydatabase`.`staff` (`code`, `name`, `username`) VALUES ('Staff2', 'Nhân viên 2', 'staff2');
INSERT INTO `mydatabase`.`staff` (`code`, `name`, `username`) VALUES ('Staff3', 'Nhân viên 3', 'staff3');
