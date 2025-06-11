CREATE TABLE storage_unit (
                              id SERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              parent_id INT REFERENCES storage_unit(id)
);

CREATE TABLE items (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(100) NOT NULL,
                                quantity INT NOT NULL,
                                storage_unit_id INT NOT NULL REFERENCES storage_unit(id)
);