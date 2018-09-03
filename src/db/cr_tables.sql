CREATE TABLE `quantum`.`transaction` (
  `tranId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `secId` INT UNSIGNED NOT NULL,
  `userId` INT UNSIGNED NOT NULL,
  `tranDate` DATE NOT NULL,
  `type` VARCHAR(3) NOT NULL,
  `shares` DOUBLE NOT NULL,
  `price` DOUBLE NOT NULL,
  PRIMARY KEY (`tranId`),
  UNIQUE INDEX `tranId_UNIQUE` (`tranId` ASC));

CREATE TABLE `quantum`.`security` (
  `secId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `basketId` INT UNSIGNED NOT NULL,
  `symbol` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`secId`),
  UNIQUE INDEX `secId_UNIQUE` (`secId` ASC));

CREATE TABLE `quantum`.`basket` (
  `basketId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`basketId`),
  UNIQUE INDEX `basketId_UNIQUE` (`basketId` ASC));

