# maybe use REPLACE her to override old values: https://dev.mysql.com/doc/refman/5.7/en/replace.html
INSERT INTO `${prefix}global_settings` (`area`, `key`, `value`, `load_on_init`) VALUES
('tutorial', 'start_pos_x', '100', 1),
('tutorial', 'start_pos_y', '100', 1),
('tutorial', 'start_pos_z', '1', 1);