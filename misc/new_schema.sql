-- MySQL Script generated by MySQL Workbench
-- Sun Feb  5 14:32:47 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema cst363
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cst363
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `cst363`;
CREATE SCHEMA IF NOT EXISTS `cst363` DEFAULT CHARACTER SET utf8 ;
USE `cst363` ;

-- -----------------------------------------------------
-- Table `doctor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `doctor` (
  `doctorId` INT NOT NULL AUTO_INCREMENT,
  `doctorSSN` CHAR(9) NOT NULL,
  `doctorFirstName` VARCHAR(45) NOT NULL,
  `doctorLastName` VARCHAR(45) NOT NULL,
  `specialty` VARCHAR(45) NOT NULL,
  `practiceSinceYear` INT NOT NULL,
  PRIMARY KEY (`doctorId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patient` (
  `patientId` INT NOT NULL AUTO_INCREMENT,
  `primaryDoctorId` INT NOT NULL,
  `patientSSN` CHAR(9) NOT NULL,
  `patientFirstName` VARCHAR(45) NOT NULL,
  `patientLastName` VARCHAR(45) NOT NULL,
  `patientBirthdate` DATE NOT NULL,
  `patientState` VARCHAR(45) NOT NULL,
  `patientZip` VARCHAR(9) NOT NULL,
  `patientCity` VARCHAR(45) NOT NULL,
  `patientStreet` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`patientId`),
  INDEX `fk_patient_doctor1_idx` (`primaryDoctorId` ASC) VISIBLE,
  CONSTRAINT `fk_patient_doctor1`
    FOREIGN KEY (`primaryDoctorId`)
    REFERENCES `doctor` (`doctorId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `company` (
  `companyId` INT NOT NULL AUTO_INCREMENT,
  `companyName` VARCHAR(45) NOT NULL,
  `companyPhone` VARCHAR(17) NOT NULL,
  PRIMARY KEY (`companyId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pharmacy`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pharmacy` (
  `pharmacyId` INT NOT NULL AUTO_INCREMENT,
  `pharmacyName` VARCHAR(45) NOT NULL,
  `pharmacyPhone` VARCHAR(17) NOT NULL,
  `pharmacyZip` VARCHAR(9) NOT NULL,
  `pharmacyCity` VARCHAR(45) NOT NULL,
  `pharmacyStreet` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`pharmacyId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `drug`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `drug` (
  `drugId` INT NOT NULL AUTO_INCREMENT,
  `tradeName` VARCHAR(45) NULL,
  `genericName` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`drugId`),
  UNIQUE INDEX `tradeName_UNIQUE` (`tradeName` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `prescription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `prescription` (
  `rxNum` INT NOT NULL AUTO_INCREMENT,
  `doctorId` INT NOT NULL,
  `patientId` INT NOT NULL,
  `drugId` INT NOT NULL,
  `prescribeDate` DATE NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`rxNum`),
  INDEX `fk_prescription_drug1_idx` (`drugId` ASC) VISIBLE,
  INDEX `fk_prescription_patient1_idx` (`patientId` ASC) VISIBLE,
  INDEX `fk_prescription_doctor1_idx` (`doctorId` ASC) VISIBLE,
  CONSTRAINT `fk_prescription_drug1`
    FOREIGN KEY (`drugId`)
    REFERENCES `drug` (`drugId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_prescription_patient1`
    FOREIGN KEY (`patientId`)
    REFERENCES `patient` (`patientId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_prescription_doctor1`
    FOREIGN KEY (`doctorId`)
    REFERENCES `doctor` (`doctorId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pharmacyDrug`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pharmacyDrug` (
  `pharmacyId` INT NOT NULL,
  `drugId` INT NOT NULL,
  `price` INT NOT NULL,
  PRIMARY KEY (`pharmacyId`, `drugId`),
  INDEX `fk_pharmacyDrug_pharmacy1_idx` (`pharmacyId` ASC) VISIBLE,
  INDEX `fk_pharmacyDrug_drug1_idx` (`drugId` ASC) VISIBLE,
  CONSTRAINT `fk_pharmacyDrug_pharmacy1`
    FOREIGN KEY (`pharmacyId`)
    REFERENCES `pharmacy` (`pharmacyId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pharmacyDrug_drug1`
    FOREIGN KEY (`drugId`)
    REFERENCES `drug` (`drugId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `contract`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `contract` (
  `contractId` INT NOT NULL AUTO_INCREMENT,
  `pharmacyId` INT NOT NULL,
  `companyId` INT NOT NULL,
  `startDate` DATE NOT NULL,
  `endDate` DATE NOT NULL,
  `supervisorName` VARCHAR(45) NOT NULL,
  `contents` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`contractId`),
  INDEX `fk_contract_pharmacy1_idx` (`pharmacyId` ASC) VISIBLE,
  INDEX `fk_contract_company1_idx` (`companyId` ASC) VISIBLE,
  CONSTRAINT `fk_contract_pharmacy1`
    FOREIGN KEY (`pharmacyId`)
    REFERENCES `pharmacy` (`pharmacyId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_company1`
    FOREIGN KEY (`companyId`)
    REFERENCES `company` (`companyId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `fill`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fill` (
  `fillId` INT NOT NULL AUTO_INCREMENT,
  `rxNum` INT NOT NULL,
  `pharmacyId` INT NOT NULL,
  `fillDrugId` INT NOT NULL,
  `fillGenericCompanyId` INT NULL,
  `fillDate` DATE NOT NULL,
  PRIMARY KEY (`fillId`),
  INDEX `fk_fill_prescription1_idx` (`rxNum` ASC) VISIBLE,
  INDEX `fk_fill_pharmacy1_idx` (`pharmacyId` ASC) VISIBLE,
  INDEX `fk_fill_drug1_idx` (`fillDrugId` ASC) VISIBLE,
  INDEX `fk_fill_company1_idx` (`fillGenericCompanyId` ASC) VISIBLE,
  CONSTRAINT `fk_fill_prescription1`
    FOREIGN KEY (`rxNum`)
    REFERENCES `prescription` (`rxNum`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_fill_pharmacy1`
    FOREIGN KEY (`pharmacyId`)
    REFERENCES `pharmacy` (`pharmacyId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_fill_drug1`
    FOREIGN KEY (`fillDrugId`)
    REFERENCES `drug` (`drugId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_fill_company1`
    FOREIGN KEY (`fillGenericCompanyId`)
    REFERENCES `company` (`companyId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `companyMakesDrug`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `companyMakesDrug` (
  `companyId` INT NOT NULL,
  `drugId` INT NOT NULL,
  PRIMARY KEY (`companyId`, `drugId`),
  INDEX `fk_drugToCompany_drug1_idx` (`drugId` ASC) VISIBLE,
  INDEX `fk_drugToCompany_company1_idx` (`companyId` ASC) VISIBLE,
  CONSTRAINT `fk_drugToCompany_drug1`
    FOREIGN KEY (`drugId`)
    REFERENCES `drug` (`drugId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_drugToCompany_company1`
    FOREIGN KEY (`companyId`)
    REFERENCES `company` (`companyId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;