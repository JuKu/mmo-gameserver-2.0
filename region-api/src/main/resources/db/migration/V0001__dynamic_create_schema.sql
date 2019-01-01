CREATE TABLE `${prefix}character_positions` (
 `cid` int(10) NOT NULL,
 `region_id` int(10) NOT NULL DEFAULT '1',
 `instance_id` int(10) NOT NULL DEFAULT '1',
 `shard_id` int(10) NOT NULL DEFAULT '1',
 `pos_x` float(32,8) NOT NULL,
 `pos_y` float(32,8) NOT NULL,
 `pos_z` float(32,8) NOT NULL,
 `visible` int(10) NOT NULL DEFAULT '1',
 PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `${prefix}global_settings` (
 `area` varchar(64) NOT NULL,
 `key` varchar(64) NOT NULL,
 `value` varchar(255) NOT NULL,
 `load_on_init` int(10) NOT NULL DEFAULT '1',
 PRIMARY KEY (`area`,`key`),
 KEY `ix_area` (`area`),
 KEY `ix_load_on_init` (`load_on_init`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;