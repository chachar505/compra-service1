CREATE TABLE compras (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         usuario_id BIGINT NOT NULL,
                         juego_id BIGINT NOT NULL,
                         precio_pagado DECIMAL(19, 2) NOT NULL,
                         fecha_compra DATETIME NOT NULL
);