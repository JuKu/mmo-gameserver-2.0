CREATE TABLE `${prefix}regions` (
 `regionID` int(10) NOT NULL AUTO_INCREMENT,
 `instanceID` int(11) NOT NULL DEFAULT '1',
 `title` varchar(255) NOT NULL,
 `locked` int(11) NOT NULL DEFAULT '0',
 PRIMARY KEY (`regionID`,`instanceID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1