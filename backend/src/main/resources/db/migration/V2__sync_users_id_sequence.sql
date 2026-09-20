-- Realign users.id sequence after manual inserts or restores (prevents users_pkey duplicate key errors).
SELECT setval(
    pg_get_serial_sequence('users', 'id'),
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM users), 1)
);
