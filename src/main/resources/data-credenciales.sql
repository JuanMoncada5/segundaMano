-- Credenciales de prueba. TODAS usan la contraseña: Clave123*   (aquí solo se guarda su hash BCrypt, nunca la contraseña).
-- Los 4 UniCambistas ya existen en data.sql; admin@uniquindio.edu.co solo sirve para las rutas /api/admin/**.
INSERT INTO credenciales (correo_institucional, contrasena_hash, rol) VALUES
    ('ana@uqvirtual.edu.co',     '$2a$10$ww6LXkwhGN1dAXFngbmt1uDZb3LFRG8f5aub5PMj2TLsPswicaSRy', 'UNICAMBISTA'),
    ('luis@uqvirtual.edu.co',    '$2a$10$ww6LXkwhGN1dAXFngbmt1uDZb3LFRG8f5aub5PMj2TLsPswicaSRy', 'UNICAMBISTA'),
    ('sofia@uqvirtual.edu.co',   '$2a$10$ww6LXkwhGN1dAXFngbmt1uDZb3LFRG8f5aub5PMj2TLsPswicaSRy', 'UNICAMBISTA'),
    ('carlos@uniquindio.edu.co', '$2a$10$ww6LXkwhGN1dAXFngbmt1uDZb3LFRG8f5aub5PMj2TLsPswicaSRy', 'UNICAMBISTA'),
    ('admin@uniquindio.edu.co',  '$2a$10$ww6LXkwhGN1dAXFngbmt1uDZb3LFRG8f5aub5PMj2TLsPswicaSRy', 'ADMIN');
