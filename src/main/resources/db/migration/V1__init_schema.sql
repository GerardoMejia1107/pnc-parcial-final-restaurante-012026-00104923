CREATE TABLE sucursal (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    direccion  VARCHAR(255),
    telefono   VARCHAR(30)
);

CREATE TABLE mesa (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero       INTEGER NOT NULL,
    capacidad    INTEGER NOT NULL,
    estado       VARCHAR(20) NOT NULL,
    sucursal_id  BIGINT NOT NULL REFERENCES sucursal (id)
);

CREATE INDEX idx_mesa_sucursal_id ON mesa (sucursal_id);

CREATE TABLE producto (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    precio      NUMERIC(10, 2) NOT NULL,
    disponible  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_completo   VARCHAR(150) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    rol               VARCHAR(30) NOT NULL,
    sucursal_id       BIGINT REFERENCES sucursal (id)
);

CREATE INDEX idx_usuario_sucursal_id ON usuario (sucursal_id);

CREATE TABLE pedido (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id       BIGINT NOT NULL REFERENCES usuario (id),
    mesa_id          BIGINT NOT NULL REFERENCES mesa (id),
    estado           VARCHAR(20) NOT NULL,
    fecha_creacion   TIMESTAMP NOT NULL
);

CREATE INDEX idx_pedido_cliente_id ON pedido (cliente_id);
CREATE INDEX idx_pedido_mesa_id ON pedido (mesa_id);

CREATE TABLE detalle_pedido (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pedido_id         BIGINT NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
    producto_id       BIGINT NOT NULL REFERENCES producto (id),
    cantidad          INTEGER NOT NULL,
    precio_unitario   NUMERIC(10, 2) NOT NULL
);

CREATE INDEX idx_detalle_pedido_pedido_id ON detalle_pedido (pedido_id);
