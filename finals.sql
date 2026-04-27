CREATE DATABASE finals;
USE finals;
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    lastname VARCHAR(25),
    firstname VARCHAR(25),
    email VARCHAR(25) UNIQUE,
    password VARCHAR(25)
);
CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL
);
CREATE TABLE Suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255)
);
CREATE TABLE Items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id INT,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity_on_hand INT NOT NULL,
    reorder_level INT,
    FOREIGN KEY (category_id) REFERENCES Categories (category_id)
);
CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT,
    transaction_date DATETIME,
    quantity INT,
    transaction_type VARCHAR(20),
    notes TEXT,
    FOREIGN KEY (item_id) REFERENCES Items (item_id)
);
-- Add the following tables to the finals database
CREATE TABLE PurchaseOrders (
    purchase_order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME,
    supplier_id INT,
    total_amount DECIMAL(10, 2),
    /* This is a calculated field, total cost of items in the order */
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id)
);
CREATE TABLE PurchaseOrderItems (
    purchase_order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id INT,
    item_id INT,
    /* the items here are from the items table */
    quantity INT,
    /* number of items in the order */
    unit_price DECIMAL(10, 2),
    /* price per item */
    FOREIGN KEY (purchase_order_id) REFERENCES PurchaseOrders(purchase_order_id),
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);


-- Insert sample data into users table
INSERT INTO users (lastname, firstname, email, password) VALUES 
('Doe', 'John', 'john', 'john'),
('Doe', 'John', 'email', '1234'),
('Doe', 'john', '-', '-');

-- Insert sample data into Categories table
INSERT INTO Categories (category_name) VALUES 
('Electronics'),
('Books'),
('Clothing'),
('Toys'),
('Furniture'),
('Sports'),
('Beauty'),
('Automotive'),
('Garden'),
('Health'),
('Jewelry'),
('Music'),
('Office Supplies'),
('Pet Supplies'),
('Shoes'),
('Tools'),
('Video Games'),
('Watches'),
('Baby Products'),
('Groceries'),
('Movies'),
('Home Decor'),
('Kitchen'),
('Lighting'),
('Luggage'),
('Outdoors'),
('Party Supplies'),
('Personal Care'),
('Software'),
('Stationery'),
('Travel'),
('Arts & Crafts'),
('Bedding'),
('Cleaning Supplies'),
('Collectibles'),
('Computers'),
('Electronics Accessories'),
('Fitness'),
('Food & Beverage'),
('Gift Cards'),
('Handmade'),
('Industrial'),
('Instruments'),
('Magazines'),
('Medical Supplies'),
('Musical Instruments'),
('Office Furniture'),
('Photography'),
('Safety Supplies'),
('Security');

-- Insert sample data into Suppliers table
INSERT INTO Suppliers (supplier_name, contact_info) VALUES 
('Stark Industries', 'contact@starkindustries.com'),
('Wayne Enterprises', 'contact@wayneenterprises.com'),
('Oscorp', 'contact@oscorp.com'),
('Daily Planet', 'contact@dailyplanet.com'),
('Parker Industries', 'contact@parkerindustries.com'),
('SHIELD', 'contact@shield.com'),
('Avengers', 'contact@avengers.com'),
('Xavier Institute', 'contact@xavierinstitute.com'),
('Daily Bugle', 'contact@dailybugle.com'),
('LexCorp', 'contact@lexcorp.com'),
('Hammer Industries', 'contact@hammerindustries.com'),
('Roxxon', 'contact@roxxon.com'),
('Pym Technologies', 'contact@pymtechnologies.com'),
('Wakanda', 'contact@wakanda.com'),
('Kamar-Taj', 'contact@kamar-taj.com'),
('Asgard', 'contact@asgard.com'),
('Nova Corps', 'contact@novacorps.com'),
('Guardians', 'contact@guardians.com'),
('SWORD', 'contact@sword.com'),
('Hydra', 'contact@hydra.com'),
('AIM', 'contact@aim.com'),
('Hellfire Club', 'contact@hellfireclub.com'),
('Latveria', 'contact@latveria.com'),
('Genosha', 'contact@genosha.com'),
('Savage Land', 'contact@savageland.com'),
('Atlantis', 'contact@atlantis.com'),
('Krakoa', 'contact@krakoa.com'),
('Madripoor', 'contact@madripoor.com'),
('Weapon X', 'contact@weaponx.com'),
('Alpha Flight', 'contact@alphaflight.com'),
('Fantastic Four', 'contact@fantasticfour.com'),
('Inhumans', 'contact@inhumans.com'),
('Brotherhood', 'contact@brotherhood.com'),
('X-Force', 'contact@xforce.com'),
('Excalibur', 'contact@excalibur.com'),
('X-Factor', 'contact@xfactor.com'),
('New Mutants', 'contact@newmutants.com'),
('Runaways', 'contact@runaways.com'),
('Young Avengers', 'contact@youngavengers.com'),
('Champions', 'contact@champions.com'),
('Defenders', 'contact@defenders.com'),
('Midnight Sons', 'contact@midnightsons.com'),
('Thunderbolts', 'contact@thunderbolts.com'),
('Great Lakes Avengers', 'contact@greatlakesavengers.com'),
('West Coast Avengers', 'contact@westcoastavengers.com'),
('Secret Warriors', 'contact@secretwarriors.com'),
('Secret Avengers', 'contact@secretavengers.com'),
('Illuminati', 'contact@illuminati.com'),
('Infinity Watch', 'contact@infinitywatch.com'),
('Future Foundation', 'contact@futurefoundation.com');

-- Insert sample data into Items table
INSERT INTO Items (item_name, description, category_id, unit_price, quantity_on_hand, reorder_level) VALUES 
('Arc Reactor', 'Power source for Iron Man suit', 1, 1000.00, 0, 5),
('Batarang', 'Throwing weapon used by Batman', 2, 50.00, 0, 10),
('Web Shooter', 'Device used by Spider-Man', 3, 200.00, 0, 15),
('Lasso of Truth', 'Weapon used by Wonder Woman', 4, 300.00, 0, 20),
('Mjolnir', 'Hammer of Thor', 5, 500.00, 0, 25),
('Captain America Shield', 'Shield used by Captain America', 6, 400.00, 0, 30),
('Hulkbuster Armor', 'Armor used by Iron Man to fight Hulk', 7, 10000.00, 0, 2),
('Infinity Gauntlet', 'Gauntlet used to wield Infinity Stones', 8, 100000.00, 0, 1),
('Ant-Man Suit', 'Suit used by Ant-Man', 9, 1500.00, 0, 5),
('Black Panther Suit', 'Suit used by Black Panther', 10, 2000.00, 0, 5),
('Doctor Strange Cloak', 'Cloak of Levitation', 11, 2500.00, 0, 5),
('Hawkeye Bow', 'Bow used by Hawkeye', 12, 300.00, 0, 10),
('Falcon Wings', 'Wings used by Falcon', 13, 3500.00, 0, 3),
('Winter Soldier Arm', 'Arm used by Winter Soldier', 14, 5000.00, 0, 2),
('Wolverine Claws', 'Claws used by Wolverine', 15, 1000.00, 0, 5),
('Cyclops Visor', 'Visor used by Cyclops', 16, 800.00, 0, 5),
('Stormbreaker', 'Axe used by Thor', 17, 600.00, 0, 10),
('Iron Spider Suit', 'Suit used by Spider-Man', 18, 2500.00, 0, 5),
('Quantum Realm Suit', 'Suit used for Quantum Realm travel', 19, 3000.00, 0, 5),
('Vibranium Shield', 'Shield made of Vibranium', 20, 4000.00, 0, 5),
('Arc Reactor Mark II', 'Upgraded power source for Iron Man suit', 21, 1200.00, 0, 5),
('Batarang Mark II', 'Upgraded throwing weapon used by Batman', 22, 60.00, 0, 10),
('Web Shooter Mark II', 'Upgraded device used by Spider-Man', 23, 220.00, 0, 15),
('Lasso of Truth Mark II', 'Upgraded weapon used by Wonder Woman', 24, 320.00, 0, 20),
('Mjolnir Mark II', 'Upgraded hammer of Thor', 25, 550.00, 0, 25),
('Captain America Shield Mark II', 'Upgraded shield used by Captain America', 26, 440.00, 0, 30),
('Hulkbuster Armor Mark II', 'Upgraded armor used by Iron Man to fight Hulk', 27, 11000.00, 0, 2),
('Infinity Gauntlet Mark II', 'Upgraded gauntlet used to wield Infinity Stones', 28, 110000.00, 0, 1),
('Ant-Man Suit Mark II', 'Upgraded suit used by Ant-Man', 29, 1600.00, 0, 5),
('Black Panther Suit Mark II', 'Upgraded suit used by Black Panther', 30, 2200.00, 0, 5),
('Doctor Strange Cloak Mark II', 'Upgraded Cloak of Levitation', 31, 2700.00, 0, 5),
('Hawkeye Bow Mark II', 'Upgraded bow used by Hawkeye', 32, 330.00, 0, 10),
('Falcon Wings Mark II', 'Upgraded wings used by Falcon', 33, 3700.00, 0, 3),
('Winter Soldier Arm Mark II', 'Upgraded arm used by Winter Soldier', 34, 5500.00, 0, 2),
('Wolverine Claws Mark II', 'Upgraded claws used by Wolverine', 35, 1100.00, 0, 5),
('Cyclops Visor Mark II', 'Upgraded visor used by Cyclops', 36, 880.00, 0, 5),
('Stormbreaker Mark II', 'Upgraded axe used by Thor', 37, 660.00, 0, 10),
('Iron Spider Suit Mark II', 'Upgraded suit used by Spider-Man', 38, 2700.00, 0, 5),
('Quantum Realm Suit Mark II', 'Upgraded suit used for Quantum Realm travel', 39, 3300.00, 0, 5),
('Vibranium Shield Mark II', 'Upgraded shield made of Vibranium', 40, 4400.00, 0, 5),
('Arc Reactor Mark III', 'Further upgraded power source for Iron Man suit', 41, 1400.00, 0, 5),
('Batarang Mark III', 'Further upgraded throwing weapon used by Batman', 42, 70.00, 0, 10),
('Web Shooter Mark III', 'Further upgraded device used by Spider-Man', 43, 240.00, 0, 15),
('Lasso of Truth Mark III', 'Further upgraded weapon used by Wonder Woman', 44, 340.00, 0, 20),
('Mjolnir Mark III', 'Further upgraded hammer of Thor', 45, 600.00, 0, 25),
('Captain America Shield Mark III', 'Further upgraded shield used by Captain America', 46, 480.00, 0, 30),
('Hulkbuster Armor Mark III', 'Further upgraded armor used by Iron Man to fight Hulk', 47, 12000.00, 0, 2),
('Infinity Gauntlet Mark III', 'Further upgraded gauntlet used to wield Infinity Stones', 48, 120000.00, 0, 1),
('Ant-Man Suit Mark III', 'Further upgraded suit used by Ant-Man', 49, 1800.00, 0, 5),
('Black Panther Suit Mark III', 'Further upgraded suit used by Black Panther', 50, 2400.00, 0, 5);