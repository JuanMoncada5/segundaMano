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

-- ===== Entrega 3: más publicaciones ACTIVAS para que el catálogo del frontend tenga varias páginas =====
-- Todas valen más de 100.000 y no tienen ofertas: así no cambian las pruebas de búsqueda ni el ranking.
INSERT INTO publicaciones (codigo, dueno_correo, titulo, valor, estado, curso, semestre, fecha_ultima_actividad) VALUES
                                                                                                                     ('PUB-006', 'carlos@uniquindio.edu.co', 'Portátil Lenovo IdeaPad 3',     1200000.00, 'ACTIVA', NULL,             NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-007', 'luis@uqvirtual.edu.co',    'Bicicleta de montaña rin 26',    450000.00, 'ACTIVA', NULL,             NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-008', 'sofia@uqvirtual.edu.co',   'Kit de dibujo técnico',          120000.00, 'ACTIVA', 'Dibujo Técnico', '2026-2', CURRENT_DATE),
                                                                                                                     ('PUB-009', 'ana@uqvirtual.edu.co',     'Libro Física I - Serway',        110000.00, 'ACTIVA', 'Física I',       '2026-2', CURRENT_DATE),
                                                                                                                     ('PUB-010', 'carlos@uniquindio.edu.co', 'Audífonos inalámbricos',         180000.00, 'ACTIVA', NULL,             NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-011', 'sofia@uqvirtual.edu.co',   'Tablet Samsung Galaxy Tab A',    650000.00, 'ACTIVA', NULL,             NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-012', 'luis@uqvirtual.edu.co',    'Mochila para portátil',          130000.00, 'ACTIVA', NULL,             NULL,     CURRENT_DATE),
                                                                                                                     ('PUB-013', 'ana@uqvirtual.edu.co',     'Impresora multifuncional',       350000.00, 'ACTIVA', NULL,             NULL,     CURRENT_DATE);

-- ===== Entrega 3: transacciones para la funcionalidad propia (calificar) =====
-- Sus publicaciones quedan EN_PROCESO (ya se aceptó una oferta), así que no salen en el catálogo ni en el ranking.
INSERT INTO publicaciones (codigo, dueno_correo, titulo, valor, estado, curso, semestre, fecha_ultima_actividad) VALUES
                                                                                                                     ('PUB-014', 'ana@uqvirtual.edu.co',   'Lámpara de escritorio LED', 70000.00, 'EN_PROCESO', NULL, NULL, CURRENT_DATE),
                                                                                                                     ('PUB-015', 'sofia@uqvirtual.edu.co', 'Regla T de 60 cm',          50000.00, 'EN_PROCESO', NULL, NULL, CURRENT_DATE);

INSERT INTO ofertas (id, publicacion_codigo, oferente_correo, monto_propuesto, estado, transaccion_iniciada) VALUES
                                                                                                                 ('OF-011', 'PUB-014', 'luis@uqvirtual.edu.co',    65000.00, 'ACEPTADA', TRUE),
                                                                                                                 ('OF-012', 'PUB-015', 'carlos@uniquindio.edu.co', 45000.00, 'ACEPTADA', TRUE);

-- TRX-001: COMPLETADA (luis compró a ana) y nadie ha calificado todavía -> se puede calificar.
-- TRX-002: PENDIENTE (carlos le compra a sofia) -> todavía NO se puede calificar.
INSERT INTO transacciones (id, publicacion_codigo, comprador_correo, vendedor_correo, monto_acordado, zona, referencia,
                           confirmada_por_comprador, confirmada_por_vendedor, estado,
                           calificada_por_comprador, calificada_por_vendedor) VALUES
                                                                                  ('TRX-001', 'PUB-014', 'luis@uqvirtual.edu.co',    'ana@uqvirtual.edu.co',   65000.00, 'BIBLIOTECA', 'Entrada principal',
                                                                                   TRUE,  TRUE,  'COMPLETADA', FALSE, FALSE),
                                                                                  ('TRX-002', 'PUB-015', 'carlos@uniquindio.edu.co', 'sofia@uqvirtual.edu.co', 45000.00, NULL,         NULL,
                                                                                   FALSE, FALSE, 'PENDIENTE',  FALSE, FALSE);