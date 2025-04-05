CREATE SCHEMA salon;
use salon;

CREATE TABLE preferencias (
  nombre VARCHAR(20) PRIMARY KEY
);

CREATE TABLE clientes (
  correo VARCHAR(50) PRIMARY KEY,
  dpi VARCHAR(14) not null,
  contraseña VARCHAR(100) not null,
  estado VARCHAR(20) not null,
  direccion VARCHAR(50) null,
  telefono VARCHAR(20) not null,
  nombre VARCHAR(50) not null
);

CREATE TABLE servicios (
  nombre VARCHAR(40) PRIMARY KEY,
  precio double not null,
  descripcion VARCHAR(100) not null,
  catalogo BLOB null,
  imagen BLOB not null,
  duracion double not null,
  estado VARCHAR(20) not null
);

CREATE TABLE horarios (
  dia_semana INT PRIMARY KEY,
  hora_inicio INT not null,
  minutos_inicio INT not null,
  hora_fin INT not null,
  minutos_fin INT not null
);

CREATE TABLE roles (
  nombre VARCHAR(20) PRIMARY KEY
);

CREATE TABLE anuncios (
  id INT PRIMARY KEY,
  nombre VARCHAR(40) not null,
  tipo VARCHAR(20) not null,
  texto VARCHAR(100) null,
  url_video VARCHAR(100) null,
  imagen BLOB null,
  estado VARCHAR(20) not null
);

CREATE TABLE empleados (
  dpi VARCHAR(14) PRIMARY KEY,
  nombre VARCHAR(50) not null,
  descripcion VARCHAR(200) null,
  estado VARCHAR(20) not null,
  rol VARCHAR(20) not null,
  contraseña VARCHAR(100) null,
  fotografia BLOB null,
  CONSTRAINT fk_empleados_roles
    FOREIGN KEY (rol)
    REFERENCES roles (nombre)
);

CREATE TABLE perfiles (
  correo VARCHAR(50) not null,
  dpi VARCHAR(14) not null,
  fotografia BLOB null,
  hobbies VARCHAR(100) null,
  descripcion VARCHAR(100) null,
  PRIMARY KEY (correo, dpi),
  INDEX fk_perfiles_clientes_idx (dpi ASC) VISIBLE,
  CONSTRAINT fk_prefiles_clientes
    FOREIGN KEY (correo)
    REFERENCES clientes (correo)
);

CREATE TABLE asignacion_preferencias (
  nombre_preferencia VARCHAR(20) NOT NULL,
  dpi VARCHAR(14) NOT NULL,
  PRIMARY KEY (nombre_preferencia, dpi),
  INDEX fk_perfiles_asignacion_idx (dpi ASC) VISIBLE,
  CONSTRAINT fk_preferencia_asignacion
    FOREIGN KEY (nombre_preferencia)
    REFERENCES preferencias (nombre),
  CONSTRAINT fk_perfiles_asignacion
    FOREIGN KEY (dpi)
    REFERENCES perfiles (dpi)
);

CREATE TABLE reservas (
  nombre_servicio VARCHAR(40) NOT NULL,
  dpi_cliente VARCHAR(14) NOT NULL,
  dpi_empleado VARCHAR(14) NOT NULL,
  fecha date NOT NULL,
  hora INT NOT NULL,
  minutos INT not null,
  PRIMARY KEY (nombre_servicio, dpi_cliente, dpi_empleado, fecha, hora),
  INDEX fk_cliente_reserva_idx (dpi_cliente ASC) VISIBLE,
  INDEX fk_empleado_reserva_idx (dpi_empleado ASC) VISIBLE,
  INDEX fk_servicio_reserva_idx (nombre_servicio ASC) VISIBLE,
  CONSTRAINT fk_cliente_reserva
    FOREIGN KEY (dpi_cliente)
    REFERENCES perfiles (dpi),
  CONSTRAINT fk_empleado_reserva
    FOREIGN KEY (dpi_empleado)
    REFERENCES empleados (dpi),
  CONSTRAINT fk_servicio_reserva
    FOREIGN KEY (nombre_servicio)
    REFERENCES servicios (nombre)
);

CREATE TABLE asignacion_servicio (
  nombre_servicio VARCHAR(40) NOT NULL,
  dpi_empleado VARCHAR(14) NOT NULL,
  PRIMARY KEY (nombre_servicio, dpi_empleado),
  CONSTRAINT fk_empleado_asignacion
    FOREIGN KEY (dpi_empleado)
    REFERENCES empleados (dpi),
  CONSTRAINT fk_servicio_asignacion
    FOREIGN KEY (nombre_servicio)
    REFERENCES servicios (nombre)
);

CREATE TABLE publicaciones (
  dpi_empleado VARCHAR(14) NOT NULL,
  fecha_inicio date NOT NULL,
  fecha_fin date NOT NULL,
  id_anuncio INT NOT NULL,
  estado VARCHAR(20) NOT NULL,
  precio_total double NOT NULL,
  precio_faltante double NULL,
  PRIMARY KEY (dpi_empleado, fecha_inicio, id_anuncio),
  CONSTRAINT fk_empleado_publicaciones
    FOREIGN KEY (dpi_empleado)
    REFERENCES empleados (dpi),
  CONSTRAINT fk_anuncio_publicaciones
    FOREIGN KEY (id_anuncio)
    REFERENCES anuncios (id)
);

-- Ingresando roles iniciales
INSERT INTO `roles` (`nombre`) VALUES ('ADMINISTRADOR');
INSERT INTO `roles` (`nombre`) VALUES ('EMPLEADO');
INSERT INTO `roles` (`nombre`) VALUES ('SERVICIOS');
INSERT INTO `roles` (`nombre`) VALUES ('MARKETING');

-- Administrador inicial
INSERT INTO empleados (`dpi`, `nombre`, `descripcion`, `estado`, `rol`, `contraseña`) VALUES ('3140290400901', 'Administrador', 'Administrador principal del sistema', 'ACTIVO', 'ADMINISTRADOR', '3132333435363738');

-- Horarios de muestra (pueden ser modificados)
INSERT INTO `horarios` (`dia_semana`, `hora_inicio`, `minutos_inicio`, `hora_fin`, `minutos_fin`) 
VALUES ('1', '8', '0', '18', '0'),
('2', '8', '0', '18', '0'),
('3', '8', '0', '18', '0'),
('4', '8', '0', '18', '0'),
('5', '8', '0', '18', '0'),
('6', '8', '0', '18', '0'),
('7', '12', '0', '20', '0');
