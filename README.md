**ALMACÉN** (Backend)


![Java](https://img.shields.io/badge/Java-21-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql)
![Build](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)
![Docs](https://img.shields.io/badge/OpenAPI-3.0-85EA2D?logo=openapiinitiative)

---

## Tabla de contenidos

* [Arquitectura y Stack](#arquitectura-y-stack)
* [Requisitos](#requisitos)
* [Configuración](#configuración)
* [Cómo ejecutar](#cómo-ejecutar)
* [Swagger / OpenAPI](#swagger--openapi)
* [Autenticación y Roles](#autenticación-y-roles)
* [Resumen de endpoints](#resumen-de-endpoints)
* [Endpoints (detalle con ejemplos)](#endpoints-detalle-con-ejemplos)

  * [Usuario](#usuario)
  * [Dirección](#dirección)
  * [Categoría](#categoría)
  * [Producto](#producto)
  * [Carrito](#carrito)
  * [Orden](#orden)
* [Ejemplos rápidos (curl)](#ejemplos-rápidos-curl)
* [Postman](#postman)
* [Estructura del proyecto](#estructura-del-proyecto)
* [Errores y Troubleshooting](#errores-y-troubleshooting)
* [Autores](#autores)

---

## Arquitectura y Stack

* **Spring Boot 3.4.4** (Web, Security, Data JPA)
* **JWT** para autenticación stateless
* **MySQL 8.x** como base de datos
* **OpenAPI/Swagger** para documentación
* Entidades principales: `Usuario`, `Direccion`, `Categoria`, `Producto`, `Carrito`/`ItemCarrito`, `Orden`/`DetalleOrden`.

---

## Requisitos

* Java **21**
* Maven **3.9+**
* MySQL **8.x**

---

## Configuración

Crear BD (opcional, Hibernate puede crearla):

```sql
CREATE DATABASE almacen CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

Editar `src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/almacen?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Puerto (opcional)
server.port=4040

# JWT (cambiar por un secreto robusto)
jwt.secret=mi_super_secreto_256bits
```

---

## Cómo ejecutar

```bash
# compilar (sin tests)
mvn -q clean package -DskipTests

# levantar el server (por defecto en 4040)
mvn spring-boot:run -D"spring-boot.run.arguments=--server.port=4040"
```

---

## Swagger / OpenAPI

* UI: **[http://localhost:4040/swagger-ui/index.html](http://localhost:4040/swagger-ui/index.html)**
* JSON: **[http://localhost:4040/v3/api-docs](http://localhost:4040/v3/api-docs)**

En Swagger, usá el botón **Authorize** y pegá `Bearer {token}` tras hacer login.

---

## Autenticación y Roles

> Todos los endpoints (salvo **login**, **registro**, catálogo y Swagger) requieren **JWT**.
> Header: `Authorization: Bearer {token}`

* **ADMIN**: acceso total.
* **USER**: sólo sus datos, direcciones, carrito y órdenes.
* Endpoints públicos: `/usuarios/login`, `/usuarios` (registro), `/producto` y `/producto/catalogo` (GET), `/v3/api-docs/**`, `/swagger-ui/**`.

---

## Resumen de endpoints

| Recurso     | Base path      | Descripción                                  |
| ----------- | -------------- | -------------------------------------------- |
| Usuarios    | `/usuarios`    | Registro, login, perfil, CRUD (sin password) |
| Direcciones | `/direcciones` | CRUD de direcciones del usuario autenticado  |
| Categorías  | `/categorias`  | Listado público y CRUD (solo ADMIN)          |
| Productos   | `/producto`    | Catálogo, filtros, y administración          |
| Carrito     | `/carritos`    | Crear/obtener/vaciar + agregar/quitar items  |
| Órdenes     | `/ordenes`     | Finalizar compra y consultar historial       |

---

## Endpoints (detalle con ejemplos)

### Usuario

> Requieren JWT salvo **login** y **registro**.

#### POST `/usuarios/login` — Autentica usuario y devuelve JWT

**Request**

```json
{
  "username": "usuario1",
  "password": "1234"
}
```

**Response**

```json
{
  "token": "jwt_token_aqui",
  "usuario": {
    "id": 1,
    "username": "usuario1",
    "email": "usuario1@mail.com",
    "nombre": "Juan",
    "apellido": "Perez",
    "rol": "USER"
  }
}
```

#### POST `/usuarios` — Registro

**Request**

```json
{
  "username": "usuario2",
  "email": "usuario2@mail.com",
  "password": "abcd",
  "nombre": "Ana",
  "apellido": "Gomez",
  "rol": "USER"
}
```

**Response**

```json
{
  "id": 2,
  "username": "usuario2",
  "email": "usuario2@mail.com",
  "nombre": "Ana",
  "apellido": "Gomez",
  "rol": "USER",
  "fecha_registro": "2024-05-01T12:00:00"
}
```

#### GET `/usuarios` — Lista usuarios (sin password)

**Response**

```json
[
  {
    "id": 1,
    "username": "usuario1",
    "email": "usuario1@mail.com",
    "nombre": "Juan",
    "apellido": "Perez",
    "rol": "USER",
    "fecha_registro": "2024-05-01T12:00:00"
  }
]
```

#### GET `/usuarios/{id}` — Obtiene usuario por ID

#### PUT `/usuarios/{id}` — Reemplazo total (sin password)

**Request**

```json
{
  "username": "usuario1",
  "email": "usuario1@mail.com",
  "nombre": "Juan",
  "apellido": "Perez",
  "rol": "USER"
}
```

#### PATCH `/usuarios/{id}` — Actualiza parcialmente (sin password)

**Request**

```json
{
  "nombre": "Juan Carlos"
}
```

#### PUT `/usuarios/password` — Cambiar contraseña (autenticado)

**Request**

```json
{
  "contrasenaActual": "1234",
  "nuevaContrasena": "nueva123"
}
```

**Response**

```json
"La contraseña fue actualizada correctamente."
```

#### DELETE `/usuarios/{id}` — Eliminar usuario

#### GET `/usuarios/exists/username/{username}` — `true/false`

#### GET `/usuarios/exists/email/{email}` — `true/false`

#### GET `/usuarios/rol/{rol}` — Usuarios por rol

#### GET `/usuarios/me` — Perfil autenticado

---

### Dirección (todas requieren JWT)

#### GET `/direcciones` — Lista del usuario

**Response**

```json
[
  {
    "id": 1,
    "calle": "Av. Siempre Viva",
    "numero": "742",
    "pisoDepto": "2B",
    "ciudad": "Springfield",
    "provincia": "Buenos Aires",
    "codigoPostal": "1234",
    "tipoVivienda": "DEPARTAMENTO"
  }
]
```

#### POST `/direcciones` — Crear

**Request**

```json
{
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "2B",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "DEPARTAMENTO"
}
```

**Response**

```json
{
  "id": 2,
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "2B",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "DEPARTAMENTO"
}
```

#### PUT `/direcciones/{id}` — Actualizar (si te pertenece)

#### DELETE `/direcciones/{id}` — Eliminar (si te pertenece)

**Notas**

* No envíes `usuario` en el body: se asigna automáticamente.
* 401 sin token, 403 si no te pertenece, 404 si no existe.

---

### Categoría

* **Lectura** (GET): ADMIN, USER y visitantes.
* **Escritura** (POST/PUT/DELETE): sólo **ADMIN**.

#### GET `/categorias` — Lista (paginado opcional `page`, `size`)

**Response**

```json
[
  {
    "id": 1,
    "nombre": "Bebidas",
    "subcategorias": [
      { "id": 2, "nombre": "Gaseosas" }
    ]
  }
]
```

#### GET `/categorias/{id}` — Detalle

**Response**

```json
{
  "id": 1,
  "nombre": "Alimentos",
  "subcategorias": [
    { "id": 5, "nombre": "Pastas" }
  ]
}
```

#### POST `/categorias` *(ADMIN)* — Crear

**Request**

```json
{ "nombre": "Lácteos", "parentId": null }
```

**Response**

```json
{ "id": 3, "nombre": "Lácteos", "parent": null }
```

**Validaciones**

* `parentId` debe ser válido si se envía.
* El nombre no puede repetirse al mismo nivel.
  **Errores comunes**: 409 (duplicado), 404 (no existe).

#### PUT `/categorias/{id}` *(ADMIN)* — Reemplazo total

**Request**

```json
{ "nombre": "Snacks", "parentId": null }
```

#### DELETE `/categorias/{id}` *(ADMIN)*

---

### Producto

#### GET `/producto` — Catálogo con filtros opcionales

Parámetros: `nombre`, `marca`, `categoriaId`, `precioMin`, `precioMax`, `page`, `size`.

**Ejemplo**

```
GET /producto?nombre=leche&marca=LaSerenisima&categoriaId=2&precioMin=100&precioMax=200&page=0&size=10
```

Otras consultas:

* **GET** `/producto/catalogo` (público, mismos filtros)
* **GET** `/producto/id/{id}`
* **GET** `/producto/nombre/{nombreProducto}`
* **GET** `/producto/marca/{marca}`
* **GET** `/producto/categoria/{categoriaId}`
* **GET** `/producto/precio?precioMin=..&precioMax=..`

Administración:

* **POST** `/producto` *(ADMIN/Vendedor)*
* **PUT** `/producto/{id}` *(ADMIN/Vendedor)*
* **DELETE** `/producto/{id}` *(ADMIN/Vendedor)*

---

### Carrito (JWT requerido)

**Reglas**

* Cada usuario maneja **su** carrito.
* Se crea automáticamente con la primera operación.

**Estados**

* `VACIO` (sin productos)
* `ACTIVO` (con productos)

#### POST `/carritos` — Crea vacío (si no existe)

**Response**

```json
{ "id": 1, "estado": "VACIO", "items": [] }
```

#### GET `/carritos` — Obtiene (o crea)

**Response**

```json
{
  "id": 1,
  "estado": "ACTIVO",
  "items": [
    { "productoId": 10, "nombre": "Leche", "cantidad": 2, "precioUnitario": 120.50, "subtotal": 241.00 },
    { "productoId": 15, "nombre": "Pan",   "cantidad": 1, "precioUnitario": 80.00,  "subtotal": 80.00 }
  ]
}
```

#### PATCH `/carritos/productos/{productoId}?cantidad=1` — Agregar/incrementar

**Response**

```json
{
  "id": 1,
  "estado": "ACTIVO",
  "items": [
    { "productoId": 10, "nombre": "Leche", "cantidad": 3, "precioUnitario": 120.50, "subtotal": 361.50 }
  ]
}
```

#### DELETE `/carritos/items/{itemId}` — Quitar item (o reducir)

#### DELETE `/carritos` — Vaciar carrito

**Validaciones y errores**

* 404 Producto no encontrado
* 400 Stock insuficiente / cantidad ≤ 0 / producto desactivado / carrito vacío
* 409 Carrito ya existe

**Extra**

* El **precio se “congela”** al agregar al carrito.

---

### Orden (JWT requerido)

**Reglas**

* Las órdenes se crean al finalizar compra del carrito.
* Se descuenta stock automáticamente.
* El usuario sólo ve sus órdenes.

**Estado**

* `FINALIZADA`

#### POST `/ordenes` — Finalizar compra

**Request (envío a domicilio)**

```json
{ "direccionId": 123 }
```

**Request (retiro en tienda)**

```json
{}
```

**Response**

```json
{
  "id": 1,
  "fecha": "2024-05-15T14:30:00",
  "estado": "FINALIZADA",
  "subtotal": 321.00,
  "descuentoTotal": 0.00,
  "total": 321.00,
  "direccion": "Calle 123, Ciudad",
  "items": [
    { "productoId": 10, "nombre": "Leche", "cantidad": 2, "precioUnitario": 120.50, "subtotal": 241.00 },
    { "productoId": 15, "nombre": "Pan",   "cantidad": 1, "precioUnitario": 80.00,  "subtotal": 80.00  }
  ]
}
```

#### GET `/ordenes/usuarios/{id}` — Historial del usuario

**Response**

```json
[
  {
    "id": 1,
    "fecha": "2024-05-15T14:30:00",
    "estado": "FINALIZADA",
    "total": 321.00,
    "items": [ { "productoId": 10, "nombre": "Leche", "cantidad": 2, "precioUnitario": 120.50, "subtotal": 241.00 } ]
  }
]
```

#### GET `/ordenes/{ordenId}/usuarios/{id}` — Detalle de una orden

**Errores comunes**: 400 (carrito vacío/stock), 403 (no te pertenece), 404 (no existe).

**Extra**

* Precios se **bloquean** al crear la orden.
* Las órdenes son **inmutables**.

---

## Ejemplos rápidos (curl)

```bash
# Registro
curl -X POST http://localhost:4040/usuarios \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario1","email":"u1@mail.com","password":"1234","nombre":"Juan","apellido":"Perez","rol":"USER"}'

# Login
curl -X POST http://localhost:4040/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario1","password":"1234"}'

# Usar token
TOKEN="PEGA_TU_TOKEN_ACA"

# Perfil
curl http://localhost:4040/usuarios/me -H "Authorization: Bearer $TOKEN"

# Crear dirección
curl -X POST http://localhost:4040/direcciones \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"calle":"Av. Siempre Viva","numero":"742","pisoDepto":"2B","ciudad":"Springfield","provincia":"Buenos Aires","codigoPostal":"1234","tipoVivienda":"DEPARTAMENTO"}'
```

---

## Postman

* Podés **importar** la API desde el link OpenAPI: `http://localhost:4040/v3/api-docs`
  (Postman → *Import* → *Link* → pegás la URL).
* Sugerencia de Environment:

  * `baseUrl = http://localhost:4040`
  * `token = (se completa tras login)`

---

## Estructura del proyecto

```
src/
 └─ main/
     ├─ java/com/uade/tpo/almacen/
     │   ├─ controller/    # REST controllers
     │   ├─ entity/        # Entidades JPA
     │   ├─ repository/    # Spring Data Repos
     │   ├─ service/       # Lógica de negocio
     │   └─ security/      # JwtUtil, JwtRequestFilter, SecurityConfig
     └─ resources/
         ├─ application.properties
         └─ data.sql / schema.sql (opcional)
```

---

## Errores y Troubleshooting

* **401 Unauthorized**: falta/expiró el token.
* **403 Forbidden**: querés acceder/modificar algo que no te pertenece o sin rol.
* **404 Not Found**: recurso inexistente.
* **409 Conflict**: duplicados (usuario/categoría).
* **400 Bad Request**: validaciones (stock insuficiente, cantidad ≤ 0, etc.).

**Swagger 500 (`ControllerAdviceBean`)**
Asegurarse de usar `springdoc-openapi-starter-webmvc-ui` **2.x** con Spring Boot **3.x** (ya configurado).

**Swagger 403**
Verificar en `SecurityConfig` que estén permitidos:

```
/v3/api-docs/**, /swagger-ui/**, /swagger-ui.html
```

---

## Autores

**Grupo 13 — ALMACÉN**

* Integrantes: Valentino Gonzalo Diaz Imbernon *LU* 1203006
* Materia: Aplicaciones Interactivas – UADE
* Docente: Gisele Gabriela Cuello



