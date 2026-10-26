-- Datos de prueba conocidos. Solo se cargan con H2 (en el perfil mariadb: spring.sql.init.mode=never).
-- Las pruebas de repositorio, seguridad e integración dependen de estos datos: si los cambian, revisen las pruebas.

INSERT INTO uni_cambistas (correo_institucional, nombre, verificado, promedio_estrellas, total_transacciones) VALUES
                                                                                                                  ('ana@uqvirtual.edu.co',    'Ana',    TRUE, 4.8, 5),
                                                                                                                  ('luis@uqvirtual.edu.co',   'Luis',   TRUE, 4.2, 3),
                                                                                                                  ('sofia@uqvirtual.edu.co',  'Sofía',  TRUE, 5.0, 0),
                                                                                                                  ('carlos@uniquindio.edu.co','Carlos', TRUE, 2.5, 6);

-- PUB-001: 3 ofertas | PUB-003: 2 | PUB-002: 1 | PUB-004: 0 | PUB-005: 4 pero ELIMINADA (no entra al ranking)
INSERT INTO publicaciones (codigo, dueno_correo, titulo, valor, estado, curso, semestre, fecha_ultima_actividad) VALUES
                                                                                                                     ('PUB-001', 'ana@uqvirtual.edu.co',     'Calculadora Casio fx-991',   150000.00, 'ACTIVA',    NULL,        NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-002', 'luis@uqvirtual.edu.co',    'Libro Cálculo I - Stewart',   80000.00, 'ACTIVA',    'Cálculo I', '2026-2', CURRENT_DATE),
                                                                                                                     ('PUB-003', 'sofia@uqvirtual.edu.co',   'Bata de laboratorio talla M', 40000.00, 'ACTIVA',    NULL,        NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-004', 'ana@uqvirtual.edu.co',     'Silla de escritorio',         60000.00, 'ACTIVA',    NULL,        NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-005', 'carlos@uniquindio.edu.co', 'Monitor 24 pulgadas',        300000.00, 'ELIMINADA', NULL,        NULL,     CURRENT_DATE);

INSERT INTO ofertas (id, publicacion_codigo, oferente_correo, monto_propuesto, estado, transaccion_iniciada) VALUES
                                                                                                                 ('OF-001', 'PUB-001', 'luis@uqvirtual.edu.co',    100000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-002', 'PUB-001', 'sofia@uqvirtual.edu.co',   120000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-003', 'PUB-001', 'carlos@uniquindio.edu.co',  90000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-004', 'PUB-002', 'ana@uqvirtual.edu.co',      60000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-005', 'PUB-003', 'ana@uqvirtual.edu.co',      30000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-006', 'PUB-003', 'luis@uqvirtual.edu.co',     25000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-007', 'PUB-005', 'ana@uqvirtual.edu.co',     200000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-008', 'PUB-005', 'luis@uqvirtual.edu.co',    210000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-009', 'PUB-005', 'sofia@uqvirtual.edu.co',   220000.00, 'PENDIENTE', FALSE),
                                                                                                                 ('OF-010', 'PUB-005', 'ana@uqvirtual.edu.co',     230000.00, 'PENDIENTE', FALSE);