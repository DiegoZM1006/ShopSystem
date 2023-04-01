DROP DATABASE IF EXISTS db_mysystem;
CREATE DATABASE db_mysystem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_mysystem;

-- START: CREATE TABLES

CREATE TABLE `STORE` (
    `ID` int(11) NOT NULL,
    `NAME` varchar(50) NOT NULL,
    `PHONE_NUMBER` varchar(50) NOT NULL,
    `STREET_ADDRESS` varchar(50) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `STORAGE` (
    `ID` int(11) NOT NULL,
    `NAME` varchar(30) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `PRODUCT` (
    `ID` int(11) NOT NULL,
    `ID_STORE` int(11) NOT NULL,
    `ID_FAMILY` int(11) NOT NULL,
    `DESCRIPTION` varchar(100) NOT NULL,
    `PRICE` double(10, 2) NOT NULL,
    `AMOUNT` int(11) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `FAMILY` (
    `ID` int(11) NOT NULL,
    `NAME` varchar(50) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `CREDIT` (
    `ID` int(11) NOT NULL,
    `ID_CLIENT` int(11) NOT NULL,
    `ID_PRODUCT` int(11) NOT NULL,
    `AMOUNT` int(11) NOT NULL,
    `STATE` boolean NOT NULL,
    `DATE` date NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

CREATE TABLE `CREDIT_DATE` (
    `ID` int(11) NOT NULL,
    `ID_USER` int(11) NOT NULL,
    `ID_CREDIT` int(11) NOT NULL,
    `PAY` double(10, 2) NOT NULL,
    `PAYABLE` double(10, 2) NOT NULL,
    `TOTAL` double(10, 2) NOT NULL,
    `DATE` date NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

CREATE TABLE `CLIENT` (
    `ID` int(11) NOT NULL,
    `NAME` varchar(50) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

CREATE TABLE `USER` (
    `ID` int(11) NOT NULL,
    `USERNAME` varchar(50) NOT NULL,
    `NAME` varchar(30) NOT NULL,
    `LASTNAME` varchar(30) NOT NULL,
    `PASSWORD` varchar(50) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `PERMIT` (
    `ID` int(11) NOT NULL,
    `NAME` varchar(30) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `USER_PERMIT` (
    `ID` int(11) NOT NULL,
    `ID_USER` int(11) NOT NULL,
    `ID_PERMIT` int(11) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `PACKAGE` (
    `ID` int(11) NOT NULL,
    `ID_USER` int(11) NOT NULL,
    `DESCRIPTION` varchar(200) NOT NULL,
    `SHIPPING_DATE` date NOT NULL,
    `SHIPPING_TIME` time NOT NULL,
    `DATE_RECEIVED` date,
    `TIME_RECEIVED` time,
    `STATE` boolean NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

CREATE TABLE `PACKAGE_DETAIL` (
    `ID` int(11) NOT NULL,
    `ID_PRODUCT` int(11) NOT NULL,
    `ID_PACKAGE` int(11) NOT NULL,
    `AMOUNT` int(11) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

CREATE TABLE `SALES` (
    `ID` int(11) NOT NULL,
    `ID_CLIENT` int(11) NOT NULL,
    `ID_USER` int(11) NOT NULL,
    `RECEIVED` double(10, 2) NOT NULL,
    `RETURNED` double(10, 2) NOT NULL,
    `TOTAL` double(10, 2) NOT NULL,
    `DATE` date NOT NULL,
    `TIME` time NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

CREATE TABLE `SALES_DETAIL` (
    `ID` int(11) NOT NULL,
    `ID_PRODUCT` int(11) NOT NULL,
    `ID_SALES` int(11) NOT NULL,
    `AMOUNT` int(11) NOT NULL,
    `PRICE` double(10, 2) NOT NULL,
    `TOTAL_PRICE` double(10, 2) NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ;

-- END: CREATE TABLE

-- START: ADD PRIMARY KEY AND AUTO_INCREMENT

ALTER TABLE `STORE` ADD PRIMARY KEY (ID);
ALTER TABLE `STORE` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `STORAGE` ADD PRIMARY KEY (ID);
ALTER TABLE `STORAGE` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `FAMILY` ADD PRIMARY KEY (ID);
ALTER TABLE `FAMILY` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `PRODUCT` ADD PRIMARY KEY (ID);
ALTER TABLE `PRODUCT` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `CREDIT` ADD PRIMARY KEY (ID);
ALTER TABLE `CREDIT` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `CREDIT_DATE` ADD PRIMARY KEY (ID);
ALTER TABLE `CREDIT_DATE` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `PACKAGE` ADD PRIMARY KEY (ID);
ALTER TABLE `PACKAGE` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `PACKAGE_DETAIL` ADD PRIMARY KEY (ID);
ALTER TABLE `PACKAGE_DETAIL` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `SALES_DETAIL` ADD PRIMARY KEY (ID);
ALTER TABLE `SALES_DETAIL` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `SALES` ADD PRIMARY KEY (ID);
ALTER TABLE `SALES` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `CLIENT` ADD PRIMARY KEY (ID);
ALTER TABLE `CLIENT` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `USER` ADD PRIMARY KEY (ID);
ALTER TABLE `USER` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `PERMIT` ADD PRIMARY KEY (ID);
ALTER TABLE `PERMIT` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

ALTER TABLE `USER_PERMIT` ADD PRIMARY KEY (ID);
ALTER TABLE `USER_PERMIT` MODIFY ID int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT = 1;

-- END: ADD PRIMARY KEY AND AUTO_INCREMENT

-- START : FOREIGN KEY

ALTER TABLE `PRODUCT` ADD FOREIGN KEY (ID_STORE) REFERENCES STORAGE(ID);
ALTER TABLE `PRODUCT` ADD FOREIGN KEY (ID_FAMILY) REFERENCES FAMILY(ID);

ALTER TABLE `CREDIT` ADD FOREIGN KEY (ID_PRODUCT) REFERENCES PRODUCT(ID);
ALTER TABLE `CREDIT` ADD FOREIGN KEY (ID_CLIENT) REFERENCES CLIENT(ID);

ALTER TABLE `PACKAGE` ADD FOREIGN KEY (ID_USER) REFERENCES USER(ID);

ALTER TABLE `PACKAGE_DETAIL` ADD FOREIGN KEY (ID_PRODUCT) REFERENCES PRODUCT(ID);
ALTER TABLE `PACKAGE_DETAIL` ADD FOREIGN KEY (ID_PACKAGE) REFERENCES PACKAGE(ID);

ALTER TABLE `CREDIT_DATE` ADD FOREIGN KEY (ID_USER) REFERENCES USER(ID);
ALTER TABLE `CREDIT_DATE` ADD FOREIGN KEY (ID_CREDIT) REFERENCES CREDIT(ID);

ALTER TABLE `SALES_DETAIL` ADD FOREIGN KEY (ID_PRODUCT) REFERENCES PRODUCT(ID);
ALTER TABLE `SALES_DETAIL` ADD FOREIGN KEY (ID_SALES) REFERENCES SALES(ID);

ALTER TABLE `SALES` ADD FOREIGN KEY (ID_USER) REFERENCES USER(ID);
ALTER TABLE `SALES` ADD FOREIGN KEY (ID_CLIENT) REFERENCES CLIENT(ID);

ALTER TABLE `USER_PERMIT` ADD FOREIGN KEY (ID_USER) REFERENCES USER(ID);
ALTER TABLE `USER_PERMIT` ADD FOREIGN KEY (ID_PERMIT) REFERENCES PERMIT(ID);

-- END : FOREIGN KEY

-- START: INSERT DATA

INSERT INTO `STORE` (`NAME`, `PHONE_NUMBER`, `STREET_ADDRESS`)  values ('Kath Urban Style', '0962750018', 'XXXXXXXXXXXXXXX');

INSERT INTO `USER` (`ID`,`USERNAME`, `NAME`, `LASTNAME`, `PASSWORD`) values (1, 'admin', 'Diego', 'Mueses', '21232f297a57a5a743894a0e4a801fc3');

INSERT INTO `PERMIT` (`ID`, `NAME`) values (1, 'Dashboard'), (2, 'Reports'), (3, 'Sell'), (4, 'Inventory'), (5, 'Packages')
                                           , (6, 'Clients'), (7, 'Credits'), (8, 'Configuration'), (9, 'Users');

INSERT INTO `USER_PERMIT` (`ID`, `ID_USER`, `ID_PERMIT`) values (1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5), (6, 1, 6)
                                                                , (7, 1, 7), (8, 1, 8), (9, 1, 9);


-- END: INSERT DATA