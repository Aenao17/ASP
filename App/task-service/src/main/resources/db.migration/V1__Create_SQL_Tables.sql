CREATE TABLE tasks (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) UNIQUE,
                       description TEXT,
                       status VARCHAR(50),
                       owner_id VARCHAR(255),
                       created_at TEXT,
                       deadline TEXT
);

-- volunteers collection: one row per volunteer/string
CREATE TABLE tasks_volunteers (
                                  task_id   INTEGER NOT NULL
                                      REFERENCES tasks(id)
                                          ON DELETE CASCADE,
                                  volunteer VARCHAR(255) NOT NULL
);

-- subTasks collection: storing each element as JSONB
CREATE TABLE tasks_sub_tasks (
                                 task_id  INTEGER NOT NULL
                                     REFERENCES tasks(id)
                                         ON DELETE CASCADE,
                                 sub_task JSONB NOT NULL
);