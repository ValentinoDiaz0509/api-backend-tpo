# TP Grupo 04: Supermercado

## Endpoints de Usuario
> Todos los endpoints (excepto login y registro) requieren autenticación JWT.  
> Agregar header: `Authorization: Bearer {token}` (setear en Postman o Insomnia).

### Autenticación
- El token se obtiene al hacer login.
- El rol de usuario se asigna automáticamente al registrarse.
- El rol de administrador se asigna manualmente en la base de datos.
- El administrador accede a todos los endpoints.
- El usuario accede solo a sus propios endpoints de usuario y dirección.
- El usuario no puede acceder a endpoints de administrador ni a datos de otros usuarios.

### POST /usuarios/login
Autentica usuario y devuelve JWT.
#### Request:
```json
{
  "username": "usuario1",
  "password": "1234"
}
```
#### Response:
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

---

### POST /usuarios
Crea un nuevo usuario.
#### Request:
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
#### Response:
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

---

### GET /usuarios
Lista todos los usuarios (sin password).
#### Response:
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

---

### GET /usuarios/{id}
Obtiene usuario por ID (sin password).

---

### PUT /usuarios/{id}
Actualiza usuario (reemplazo total, sin password).
#### Request:
```json
{
  "username": "usuario1",
  "email": "usuario1@mail.com",
  "nombre": "Juan",
  "apellido": "Perez",
  "rol": "USER"
}
```

---

### PATCH /usuarios/{id}
Actualiza parcialmente usuario (sin password).
#### Request:
```json
{
  "nombre": "Juan Carlos"
}
```

---

### PUT /usuarios/password
Cambia la contraseña del usuario autenticado.
#### Request:
```json
{
  "contrasenaActual": "1234",
  "nuevaContrasena": "nueva123"
}
```
#### Response:
`"La contraseña fue actualizada correctamente."`

---

### DELETE /usuarios/{id}
Elimina usuario por ID.

---

### GET /usuarios/exists/username/{username}
Verifica si existe un usuario por username.  
#### Response: `true` o `false`

---

### GET /usuarios/exists/email/{email}
Verifica si existe un usuario por email.  
#### Response: `true` o `false`

---

### GET /usuarios/rol/{rol}
Lista usuarios por rol.

---

### GET /usuarios/me
Devuelve el perfil del usuario autenticado.

---

## Endpoints de Dirección

> Todos requieren autenticación JWT.

### GET /direcciones
Lista todas las direcciones del usuario autenticado.
#### Response:
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
    "tipoVivienda": "departamento"
  }
]
```

---

### POST /direcciones
Crea una nueva dirección para el usuario autenticado.
#### Request:
```json
{
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "2B",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "departamento"
}
```
#### Response:
```json
{
  "id": 2,
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "2B",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "departamento"
}
```

---

### PUT /direcciones/{id}
Actualiza una dirección (solo si pertenece al usuario).
#### Request:
```json
{
  "calle": "Av. Siempre Viva",
  "numero": "742",
  "pisoDepto": "3C",
  "ciudad": "Springfield",
  "provincia": "Buenos Aires",
  "codigoPostal": "1234",
  "tipoVivienda": "departamento"
}
```

---

### DELETE /direcciones/{id}
Elimina una dirección (solo si pertenece al usuario).

---

**Notas:**
- No envíes el campo `usuario` en el body de dirección, se asigna automáticamente.
- Si intentas modificar/eliminar una dirección que no te pertenece, recibirás 403 Forbidden.
- Si no estás autenticado, recibirás 401 Unauthorized.
- Si el usuario no existe, recibirás 404 Not Found.
- Si el rol no es válido, recibirás 400 Bad Request.
- Si el usuario ya existe, recibirás 409 Conflict.
- Si el email no es válido, recibirás 400 Bad Request.
- Si la contraseña no cumple con los requisitos, recibirás 400 Bad Request.
- Si el token es inválido o ha expirado, recibirás 401 Unauthorized.

---

## Endpoints de Categoría

### Autenticación y permisos

- El usuario o No usuario puede consultar categorías, pero no puede modificarlas.
- Solo el administrador puede crear, editar o eliminar categorías.

## Modelo de solicitud

### `CategoryRequest`
- nombre: nombre de la categoría (String)
- parentId: ID de la categoría padre (Integer) - puede ser null si es una categoria principal.

### Endpoints:
   
    ==========

### GET /categorias
---------------
Lista todas las categorías y subcategorías.

Acceso: ADMIN, USER y NoUser

Query params opcionales:
- page (int): número de página (default: 0)
- size (int): cantidad de elementos por página (default: 10)

//categorias?page=0&size=5 o //categorias
Response:
[
  {
    "id": 1,
    "nombre": "Bebidas",
    "subcategorias": [
      {
        "id": 2,
        "nombre": "Gaseosas"
      }
    ]
  }
]

--------------------
### GET /categorias/{id}
---------------------
### Obtiene una categoría por ID.

Acceso: ADMIN, USER y NoUser

Response:
{
  "id": 1,
  "nombre": "Alimentos",
  "subcategorias": [
    {
      "id": 5,
      "nombre": "Pastas"
    }
  ]
}

Errores:
- 404 Not Found: Categoría inexistente.

---

### POST /categorias
----------------
Crea una nueva categoría o subcategoría.

Acceso: Solo ADMIN

Request:
{
  "nombre": "Lácteos",
  "parentId": null
}

Response:
{
  "id": 3,
  "nombre": "Lácteos",
  "parent": null
}

Validaciones:
- parentId debe referenciar una categoría válida.
- El nombre no puede repetirse al mismo nivel.

Errores:
- 409 Conflict: Ya existe una categoría con ese nombre.
---

#### PUT /categorias/{id}
---------------------
### Reemplaza completamente una categoría existente.

Acceso: Solo ADMIN

Request:
{
  "nombre": "Snacks",
  "parentId": null
}

Response:
{
  "id": 4,
  "nombre": "Snacks",
  "parent": null
}

Errores: No existe la categoria que se quiere actualizar
---

## Endpoints de Producto

### GET /productos

Permite filtrar productos por cualquier combinación de los siguientes parámetros (todos opcionales):

- `nombre`: filtra por nombre (contiene, case-insensitive)
- `marca`: filtra por marca (exacto, case-insensitive)
- `categoriaId`: filtra por id de categoría
- `precioMin`: precio mínimo
- `precioMax`: precio máximo
- `page`: número de página (default: 0)
- `size`: tamaño de página (default: 20)

**Ejemplo:**
```
GET /productos?nombre=leche&marca=LaSerenisima&categoriaId=2&precioMin=100&precioMax=200&page=0&size=10
```

Devuelve una página de productos que cumplen con todos los filtros.

---

## Endpoints de Carrito
> Todos los endpoints requieren autenticación JWT.  
> Agregar header: `Authorization: Bearer {token}` (setear en Postman o Insomnia).

### Reglas de Acceso
- Solo usuarios autenticados (USER) y administradores (ADMIN) pueden acceder.
- Cada usuario solo puede acceder a su propio carrito.
- El carrito se crea automáticamente al realizar la primera operación.
- Los carritos inactivos por más de 6 horas se vacían automáticamente.

### Estados del Carrito
- `VACIO`: Carrito sin productos.
- `ACTIVO`: Carrito con al menos un producto.

---

### POST /carritos
Crea un nuevo carrito vacío para el usuario autenticado.
#### Response:
```json
{
  "id": 1,
  "usuarioId": 5,
  "estado": "VACIO",
  "items": []
}
```
### GET /carritos
Obtiene el carrito del usuario autenticado. Si no existe, crea uno vacío.

### Response:
```json
{
  "id": 1,
  "usuarioId": 5,
  "estado": "ACTIVO",
  "items": [
    {
      "productoId": 10,
      "nombre": "Leche",
      "cantidad": 2,
      "precio": 120.50
    },
    {
      "productoId": 15,
      "nombre": "Pan",
      "cantidad": 1,
      "precio": 80.00
    }
  ]
}
```

### PATCH /carritos/{productoId}
Agrega un producto al carrito o incrementa su cantidad.
Parametros: (Opcional, default=1) Cantidad a agregar
### Response
'''json
{
  "id": 1,
  "usuarioId": 5,
  "estado": "ACTIVO",
  "items": [
    {
      "productoId": 10,
      "nombre": "Leche",
      "cantidad": 3,
      "precio": 120.50
    }
  ]
}
'''
### DELETE /carritos/{productoId}
Elimina o reduce la cantidad de un producto del carrito.
Parámetros: cantidad, (Opcional, default=1) Cantidad a eliminar.

'''json
{
  "id": 1,
  "usuarioId": 5,
  "estado": "ACTIVO",
  "items": [
    {
      "productoId": 10,
      "nombre": "Leche",
      "cantidad": 1,
      "precio": 120.50
    }
  ]
}

'''

### DELETE /carritos
Vacía completamente el carrito del usuario.

Response:
'''json
{
  "id": 1,
  "usuarioId": 5,
  "estado": "VACIO",
  "items": []
}


'''
### Validaciones y Errores
Producto no encontrado: 404 Not Found

Stock insuficiente: 400 Bad Request

Cantidad inválida (≤0): 400 Bad Request

Producto desactivado: 400 Bad Request

Carrito ya existe: 409 Conflict

Carrito vacío: 400 Bad Request (al intentar eliminar productos)

### Extra:
Los precios se mantienen fijos al momento de agregar al carrito

El sistema valida stock suficiente antes de agregar productos

No se pueden agregar productos desactivados

El usuario solo puede modificar su propio carrito

Los administradores pueden acceder a todos los carritos
-----------
## Endpoints de Orden  
> Todos los endpoints requieren autenticación JWT.  
> Agregar header: `Authorization: Bearer {token}` (setear en Postman o Insomnia).  

### Reglas de Acceso  
- Solo usuarios autenticados (USER) y administradores (ADMIN) pueden acceder.  
- Cada usuario solo puede acceder a sus propias órdenes.  
- Las órdenes se crean al finalizar la compra del carrito.  
- El stock se actualiza automáticamente al crear una orden.  

### Estados de Orden  
- `FINALIZADA`: Orden completada y pagada.  

### POST /ordenes  
Finaliza la compra del carrito activo y crea una nueva orden.  
Ahora permite especificar una dirección de envío o indicar retiro en tienda.

#### Request:
Para envío a domicilio (con dirección):
```json
{
  "direccionId": 123
}
```
Para retiro en tienda:
```json
{
  "direccionId": null
}
```
O bien, puedes omitir el campo para retiro en tienda:
```json
{}
```

- `direccionId` debe ser el ID de una dirección válida del usuario, o `null` para retiro en tienda.
- Si la dirección no pertenece al usuario, devuelve 404 Not Found o 403 Forbidden.

#### Response:  
```json  
{  
  "id": 1,  
  "usuarioId": 5,  
  "fecha": "2024-05-15T14:30:00",  
  "estado": "FINALIZADA",  
  "total": 320.50,  
  "items": [  
    {  
      "productoId": 10,  
      "nombreProducto": "Leche",  
      "cantidad": 2,  
      "precioUnitario": 120.50  
    },  
    {  
      "productoId": 15,  
      "nombreProducto": "Pan",  
      "cantidad": 1,  
      "precioUnitario": 80.00  
    }  
  ]  
}
```
---

### GET /ordenes/usuarios/{id}
Obtiene todas las órdenes de un usuario específico.

Response:
'''json
[  
  {  
    "id": 1,  
    "usuarioId": 5,  
    "fecha": "2024-05-15T14:30:00",  
    "estado": "FINALIZADA",  
    "total": 320.50,  
    "items": [  
      {  
        "productoId": 10,  
        "nombreProducto": "Leche",  
        "cantidad": 2,  
        "precioUnitario": 120.50  
      }  
    ]  
  }  
]  
'''

### GET /ordenes/{ordenId}/usuarios/{id}
Obtiene una orden específica de un usuario.

'''json
{  
  "id": 1,  
  "usuarioId": 5,  
  "fecha": "2024-05-15T14:30:00",  
  "estado": "FINALIZADA",  
  "total": 320.50,  
  "items": [  
    {  
      "productoId": 10,  
      "nombreProducto": "Leche",  
      "cantidad": 2,  
      "precioUnitario": 120.50  
    }  
  ]  
}  
'''

### Validaciones y Errores
Carrito vacío: 400 Bad Request (al intentar finalizar compra)

Stock insuficiente: 400 Bad Request

Producto desactivado: 400 Bad Request

Orden no encontrada: 404 Not Found

Usuario no tiene órdenes: 404 Not Found

### Extra:
El precio de los productos se bloquea al momento de crear la orden

El stock se reduce automáticamente al crear la orden

No se pueden incluir productos desactivados

Las órdenes son inmutables una vez creadas

Los administradores pueden acceder a todas las órdenes

Los usuarios solo pueden ver sus propias órdenes







