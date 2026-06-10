INSERT INTO roles (id, role_name) VALUES (1, 'ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, role_name) VALUES (2, 'USER') ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, role_id, manage_movies, promote_admin, login, register, make_reservation, browse_movies)
VALUES (1, 1, true, true, true, true, true, true) ON CONFLICT (id) DO NOTHING;

INSERT INTO permissions (id, role_id, manage_movies, promote_admin, login, register, make_reservation, browse_movies)
VALUES (2, 2, false, false, true, true, true, true) ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, role_id, first_name, email, password)
VALUES (1, 1, 'Admin', 'admin@cinema.com', '$2a$10$8t1XttGA1fWaCv915mjE8.9Kd0S6tWewnIhSPfkAxx9jEBewCrWFu')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, role_id, first_name, email, password)
VALUES (2, 2, 'Natasa', 'natasa@cinema.com', '$2a$10$8t1XttGA1fWaCv915mjE8.9Kd0S6tWewnIhSPfkAxx9jEBewCrWFu')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, role_id, first_name, email, password)
VALUES (3, 2, 'Marko', 'marko@cinema.com', '$2a$10$8t1XttGA1fWaCv915mjE8.9Kd0S6tWewnIhSPfkAxx9jEBewCrWFu')
ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('permissions_id_seq', (SELECT MAX(id) FROM permissions));