CREATE TABLE `${prefix}regions` (
 `regionID` int(10) NOT NULL AUTO_INCREMENT,
 `instanceID` int(11) NOT NULL DEFAULT '1',
 `title` varchar(255) NOT NULL,
 `locked` int(11) NOT NULL DEFAULT '0',
 PRIMARY KEY (`regionID`,`instanceID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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