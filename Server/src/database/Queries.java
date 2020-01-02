package database;

public interface Queries {
    String DATABASE_CREATION = "CREATE SCHEMA IF NOT EXISTS `PD_Streamer` DEFAULT CHARACTER SET utf8 ;\n";


    String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS `PD_Streamer`.`User` (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `username` VARCHAR(45) NOT NULL,\n" +
            "  `password` VARCHAR(45) NOT NULL,\n" +
            "  `fullname` VARCHAR(45) NOT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\n" +
            "  UNIQUE INDEX `username_UNIQUE` (`username` ASC))\n" +
            "  ENGINE = InnoDB;";

    String CREATE_SONG_TABLE = "CREATE TABLE IF NOT EXISTS `PD_Streamer`.`Song` (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(45) NOT NULL,\n" +
            "  `artist` VARCHAR(45) NOT NULL,\n" +
            "  `album` VARCHAR(45) NOT NULL,\n" +
            "  `year` INT NOT NULL,\n" +
            "  `genre` VARCHAR(45) NOT NULL,\n" +
            "  `length` INT NOT NULL,\n" +
            "  `filelocation` VARCHAR(45) NOT NULL,\n" +
            "  `User_id` INT NOT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\n" +
            "  INDEX `fk_Song_User_idx` (`User_id` ASC),\n" +
            "  UNIQUE INDEX `name_UNIQUE` (`name` ASC),\n" +
            "  CONSTRAINT `fk_Song_User`\n" +
            "    FOREIGN KEY (`User_id`)\n" +
            "    REFERENCES `PD_Streamer`.`User` (`id`)\n" +
            "    ON DELETE NO ACTION\n" +
            "    ON UPDATE NO ACTION)\n" +
            "ENGINE = InnoDB;";

    String CREATE_PLAYLIST_TABLE = "CREATE TABLE IF NOT EXISTS `PD_Streamer`.`Playlist` (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(45) NOT NULL,\n" +
            "  `User_id` INT NOT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC),\n" +
            "  INDEX `fk_Playlist_User1_idx` (`User_id` ASC),\n" +
            "  UNIQUE INDEX `name_UNIQUE` (`name` ASC),\n" +
            "  CONSTRAINT `fk_Playlist_User1`\n" +
            "    FOREIGN KEY (`User_id`)\n" +
            "    REFERENCES `PD_Streamer`.`User` (`id`)\n" +
            "    ON DELETE NO ACTION\n" +
            "    ON UPDATE NO ACTION)\n" +
            "ENGINE = InnoDB;";

    String CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE IF NOT EXISTS `PD_Streamer`.`Playlist_has_Song` (\n" +
            "  `Playlist_id` INT NOT NULL,\n" +
            "  `Song_id` INT NOT NULL,\n" +
            "  PRIMARY KEY (`Playlist_id`, `Song_id`),\n" +
            "  INDEX `fk_Playlist_has_Song_Song1_idx` (`Song_id` ASC),\n" +
            "  INDEX `fk_Playlist_has_Song_Playlist1_idx` (`Playlist_id` ASC),\n" +
            "  CONSTRAINT `fk_Playlist_has_Song_Playlist1`\n" +
            "    FOREIGN KEY (`Playlist_id`)\n" +
            "    REFERENCES `PD_Streamer`.`Playlist` (`id`)\n" +
            "    ON DELETE NO ACTION\n" +
            "    ON UPDATE NO ACTION,\n" +
            "  CONSTRAINT `fk_Playlist_has_Song_Song1`\n" +
            "    FOREIGN KEY (`Song_id`)\n" +
            "    REFERENCES `PD_Streamer`.`Song` (`id`)\n" +
            "    ON DELETE NO ACTION\n" +
            "    ON UPDATE NO ACTION)\n" +
            "ENGINE = InnoDB;";
}
