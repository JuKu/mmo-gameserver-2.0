CREATE TABLE `${prefix}regions` (
 `regionID` int(10) NOT NULL AUTO_INCREMENT,
 `instanceID` int(11) NOT NULL DEFAULT '1',
 `title` varchar(255) NOT NULL,
 `locked` int(11) NOT NULL DEFAULT '0',
 PRIMARY KEY (`regionID`,`instanceID`)
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