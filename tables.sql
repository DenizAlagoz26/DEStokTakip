CREATE DATABASE DEStok_Takip;
USE DEStok_Takip;

CREATE TABLE users (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
	email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'user') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE locations (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    cabinet_name VARCHAR(100) NOT NULL UNIQUE,
    shelf_number VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    location_type ENUM('cabinet', 'refrigerator') NOT NULL
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE samples (
    sample_id INT AUTO_INCREMENT PRIMARY KEY,
    sample_code VARCHAR(100) NOT NULL UNIQUE,
    location_id INT NOT NULL,
    entry_date DATE NOT NULL,
    grain_size VARCHAR(50) NOT NULL,
    prepared_1kg_count INT,
    prepared_2kg_count INT,
    weight_kg DECIMAL(10,2) NOT NULL,
    au_tenor_ppm DECIMAL(10,2) NOT NULL,
    s_tenor_ppm DECIMAL(10,2) NOT NULL,
    analysis_source VARCHAR(255),
    notes TEXT,
    registered_by_user_id INT (100) NOT NULL,
    FOREIGN KEY (location_id) REFERENCES locations(location_id),
    FOREIGN KEY (registered_by_user_id) REFERENCES users(user_id)
);

CREATE TABLE item_definitions (
    definition_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category_id INT NOT NULL,
    unit VARCHAR(20) NOT NULL,
    importance_level ENUM('high', 'medium', 'low'),
    weight_class ENUM('heavy', 'medium', 'light'),
    description TEXT,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE stock_batches (
    batch_id INT AUTO_INCREMENT PRIMARY KEY,
    definition_id INT NOT NULL,
    location_id INT NOT NULL,
    current_quantity DECIMAL(10,2) NOT NULL,
    entry_date DATE NOT NULL,
    project_info VARCHAR(255),
    archive_duration_months INT,
    disposal_date DATE,
    FOREIGN KEY (definition_id) REFERENCES item_definitions(definition_id),
    FOREIGN KEY (location_id) REFERENCES locations(location_id)
);

CREATE TABLE stock_movements (
    movement_id INT AUTO_INCREMENT PRIMARY KEY,
    batch_id INT NOT NULL,
    user_id INT (100) NOT NULL,
    movement_status ENUM('in', 'out', 'transfer') NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (batch_id) REFERENCES stock_batches(batch_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


