INSERT INTO movies (id, title, duration_min, genre)
VALUES (1, 'Inception', 148, 'Sci-Fi') ON CONFLICT (id) DO NOTHING;

INSERT INTO movies (id, title, duration_min, genre)
VALUES (2, 'The Dark Knight', 152, 'Action') ON CONFLICT (id) DO NOTHING;

INSERT INTO movies (id, title, duration_min, genre)
VALUES (3, 'Interstellar', 169, 'Sci-Fi') ON CONFLICT (id) DO NOTHING;

INSERT INTO movies (id, title, duration_min, genre)
VALUES (4, 'Joker', 122, 'Drama') ON CONFLICT (id) DO NOTHING;

INSERT INTO movies (id, title, duration_min, genre)
VALUES (5, 'Avatar: The Way of Water', 192, 'Action') ON CONFLICT (id) DO NOTHING;

INSERT INTO screenings (id, movie_id, hall_id, start_time, end_time, date)
VALUES (1, 1, 1, '18:00:00', '20:28:00', '2026-06-10') ON CONFLICT (id) DO NOTHING;

INSERT INTO screenings (id, movie_id, hall_id, start_time, end_time, date)
VALUES (2, 1, 2, '21:00:00', '23:28:00', '2026-06-10') ON CONFLICT (id) DO NOTHING;

INSERT INTO screenings (id, movie_id, hall_id, start_time, end_time, date)
VALUES (3, 2, 3, '17:00:00', '19:32:00', '2026-06-10') ON CONFLICT (id) DO NOTHING;

INSERT INTO screenings (id, movie_id, hall_id, start_time, end_time, date)
VALUES (4, 3, 1, '15:00:00', '17:49:00', '2026-06-11') ON CONFLICT (id) DO NOTHING;

INSERT INTO screenings (id, movie_id, hall_id, start_time, end_time, date)
VALUES (5, 4, 4, '19:00:00', '21:02:00', '2026-06-11') ON CONFLICT (id) DO NOTHING;

INSERT INTO screenings (id, movie_id, hall_id, start_time, end_time, date)
VALUES (6, 5, 3, '20:00:00', '23:12:00', '2026-06-12') ON CONFLICT (id) DO NOTHING;

SELECT setval('movies_id_seq', (SELECT MAX(id) FROM movies));
SELECT setval('screenings_id_seq', (SELECT MAX(id) FROM screenings));