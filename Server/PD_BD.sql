-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema PD_Streamer
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema PD_Streamer
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `PD_Streamer` DEFAULT CHARACTER SET utf8 ;
USE `PD_Streamer` ;

-- -----------------------------------------------------
-- Table `PD_Streamer`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PD_Streamer`.`User` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `fullname` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PD_Streamer`.`Song`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PD_Streamer`.`Song` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `artist` VARCHAR(45) NOT NULL,
  `album` VARCHAR(45) NOT NULL,
  `year` INT NOT NULL,
  `genre` VARCHAR(45) NOT NULL,
  `length` INT NOT NULL,
  `filelocation` VARCHAR(45) NOT NULL,
  `User_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_Song_User_idx` (`User_id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  CONSTRAINT `fk_Song_User`
    FOREIGN KEY (`User_id`)
    REFERENCES `PD_Streamer`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PD_Streamer`.`Playlist`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PD_Streamer`.`Playlist` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `User_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_Playlist_User1_idx` (`User_id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  CONSTRAINT `fk_Playlist_User1`
    FOREIGN KEY (`User_id`)
    REFERENCES `PD_Streamer`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PD_Streamer`.`Playlist_has_Song`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `PD_Streamer`.`Playlist_has_Song` (
  `Playlist_id` INT NOT NULL,
  `Song_id` INT NOT NULL,
  PRIMARY KEY (`Playlist_id`, `Song_id`),
  INDEX `fk_Playlist_has_Song_Song1_idx` (`Song_id` ASC),
  INDEX `fk_Playlist_has_Song_Playlist1_idx` (`Playlist_id` ASC),
  CONSTRAINT `fk_Playlist_has_Song_Playlist1`
    FOREIGN KEY (`Playlist_id`)
    REFERENCES `PD_Streamer`.`Playlist` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Playlist_has_Song_Song1`
    FOREIGN KEY (`Song_id`)
    REFERENCES `PD_Streamer`.`Song` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;