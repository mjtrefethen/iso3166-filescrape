CREATE USER `core_db_user`
  IDENTIFIED BY 'password';

GRANT SELECT ON TABLE `core_db`.`cd_region_type` TO core_db_user;
GRANT SELECT ON TABLE `core_db`.`cd_region_path` TO core_db_user;
GRANT SELECT ON TABLE `core_db`.`cd_region` TO core_db_user;
GRANT SELECT ON TABLE `core_db`.`cd_region_path_v` TO core_db_user;
GRANT SELECT ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_user;
GRANT INSERT ON TABLE `core_db`.`cd_region_type` TO core_db_user;
GRANT UPDATE ON TABLE `core_db`.`cd_region_type` TO core_db_user;
GRANT INSERT ON TABLE `core_db`.`cd_region_path` TO core_db_user;
GRANT UPDATE ON TABLE `core_db`.`cd_region_path` TO core_db_user;
GRANT INSERT ON TABLE `core_db`.`cd_region` TO core_db_user;
GRANT UPDATE ON TABLE `core_db`.`cd_region` TO core_db_user;

CREATE USER `core_db_admin`
  IDENTIFIED BY 'password';

GRANT SELECT ON TABLE `core_db`.`cd_region_type` TO core_db_admin;
GRANT SELECT ON TABLE `core_db`.`cd_region_path` TO core_db_admin;
GRANT SELECT ON TABLE `core_db`.`cd_region` TO core_db_admin;
GRANT SELECT ON TABLE `core_db`.`cd_region_path_v` TO core_db_admin;
GRANT SELECT ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_admin;
GRANT INSERT ON TABLE `core_db`.`cd_region_type` TO core_db_admin;
GRANT UPDATE ON TABLE `core_db`.`cd_region_type` TO core_db_admin;
GRANT INSERT ON TABLE `core_db`.`cd_region_path` TO core_db_admin;
GRANT UPDATE ON TABLE `core_db`.`cd_region_path` TO core_db_admin;
GRANT INSERT ON TABLE `core_db`.`cd_region` TO core_db_admin;
GRANT UPDATE ON TABLE `core_db`.`cd_region` TO core_db_admin;
GRANT DELETE ON TABLE `core_db`.`cd_region_type` TO core_db_admin;
GRANT DELETE ON TABLE `core_db`.`cd_region_path` TO core_db_admin;
GRANT DELETE ON TABLE `core_db`.`cd_region` TO core_db_admin;

CREATE USER `core_db_report`
  IDENTIFIED BY 'password';

GRANT SELECT ON TABLE `core_db`.`cd_region_type` TO core_db_report;
GRANT SELECT ON TABLE `core_db`.`cd_region_path` TO core_db_report;
GRANT SELECT ON TABLE `core_db`.`cd_region` TO core_db_report;
GRANT SELECT ON TABLE `core_db`.`cd_region_path_v` TO core_db_report;
GRANT SELECT ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_report;

CREATE USER `core_db_dbo`
  IDENTIFIED BY 'password';

GRANT SELECT ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT SELECT ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT SELECT ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT SELECT ON TABLE `core_db`.`cd_region_path_v` TO core_db_dbo;
GRANT SELECT ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_dbo;
GRANT INSERT ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT UPDATE ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT INSERT ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT UPDATE ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT INSERT ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT UPDATE ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT DELETE ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT DELETE ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT DELETE ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT INDEX ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT ALTER ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT DROP ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT GRANT OPTION ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT CREATE ON TABLE `core_db`.`cd_region_type` TO core_db_dbo;
GRANT CREATE ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT DROP ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT GRANT OPTION ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT ALTER ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT INDEX ON TABLE `core_db`.`cd_region_path` TO core_db_dbo;
GRANT CREATE ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT DROP ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT GRANT OPTION ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT ALTER ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT INDEX ON TABLE `core_db`.`cd_region` TO core_db_dbo;
GRANT CREATE VIEW ON TABLE `core_db`.`cd_region_path_v` TO core_db_dbo;
GRANT DROP ON TABLE `core_db`.`cd_region_path_v` TO core_db_dbo;
GRANT ALTER ON TABLE `core_db`.`cd_region_path_v` TO core_db_dbo;
GRANT INDEX ON TABLE `core_db`.`cd_region_path_v` TO core_db_dbo;
GRANT CREATE VIEW ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_dbo;
GRANT DROP ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_dbo;
GRANT ALTER ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_dbo;
GRANT INDEX ON TABLE `core_db`.`cd_region_region_type_v` TO core_db_dbo;