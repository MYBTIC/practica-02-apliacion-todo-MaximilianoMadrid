# 📦 Link DockerHub

Repositorio de imagen Docker disponible en DockerHub:

🔗 [ToDoList en DockerHub](https://hub.docker.com/repository/docker/mybticpillo/todolist)

# 🚨 Excepciones: `UsuarioNotFoundException`

**Uso:** Se lanza cuando se intenta acceder a un usuario inexistente.

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException() {
        super("Usuario no encontrado");
    }
}
```
# 🔄 Resumen de Flujo de Funcionalidad

1. Usuario accede a `/registrados`.
2. `UsuarioController` llama al servicio `UsuarioService`.
3. El servicio consulta la base de datos, mapea y retorna datos.
4. Thymeleaf renderiza la vista `listaUsuarios.html`.
5. Usuario hace clic en un email ➡️ navegación a `/registrados/{id}`.
6. Se valida existencia del usuario, se muestra `descripcionUsuario.html`.
7. La navbar se actualiza dinámicamente según sesión (`ManagerUserSession`).

# 🧠 Gestión de Sesión: `ManagerUserSession`

**Clase:** `madstodolist.authentication.ManagerUserSession`  
**Responsabilidad:** Manejar el estado de autenticación del usuario en la sesión HTTP.

```java
@Component
public class ManagerUserSession {
    @Autowired
    private HttpSession session;

    // Almacena el ID del usuario en la sesión
    public void logearUsuario(Long idUsuario) {
        session.setAttribute("idUsuarioLogeado", idUsuario);
    }

    // Recupera el ID del usuario logeado (o null si no hay sesión)
    public Long usuarioLogeado() {
        return (Long) session.getAttribute("idUsuarioLogeado");
    }

    // Cierra la sesión
    public void logout() {
        session.setAttribute("idUsuarioLogeado", null);
    }
}
```
# 🧪 Testing

## 🔍 A. `UsuarioServiceTest`

```java
@Test
public void testFindAllUsuarios() {
    when(usuarioRepository.findAll()).thenReturn(Arrays.asList(new Usuario("test@test.com")));
    List<UsuarioData> usuarios = usuarioService.findAll();
    assertEquals(1, usuarios.size());
    assertEquals("test@test.com", usuarios.get(0).getEmail());
}

@Test
public void testGetUsuarioDataPublic_NoPassword() {
    Usuario usuario = new Usuario("test@test.com");
    usuario.setPassword("secreto");
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
    UsuarioData usuarioData = usuarioService.getUsuarioDataPublic(1L);
    assertNull(usuarioData.getPassword());
}
```

## 🌐 B. `UsuarioControllerTest`

```java
@Test
public void testListadoUsuarios() throws Exception {
    when(usuarioService.findAll()).thenReturn(Arrays.asList(new UsuarioData()));
    mockMvc.perform(get("/registrados"))
           .andExpect(status().isOk())
           .andExpect(view().name("listaUsuarios"))
           .andExpect(model().attributeExists("usuarios"));
}

@Test
public void testDescripcionUsuario_NotFound() {
    when(usuarioService.getUsuarioDataPublic(1L)).thenReturn(null);
    assertThrows(UsuarioNotFoundException.class, () -> {
        usuarioController.descripcionUsuario(1L, model);
    });
}
```
# 🎮 Controlador: `UsuarioController`

**Clase:** `madstodolist.controller.UsuarioController`  
**Responsabilidad:** Manejar las peticiones HTTP relacionadas con usuarios.

## 🔹 A. Listado de Usuarios `/registrados`

```java
@GetMapping("/registrados")
public String listadoUsuarios(Model model) {
    List<UsuarioData> usuarios = usuarioService.findAll();
    model.addAttribute("usuarios", usuarios);

    Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
    if (idUsuarioLogeado != null) {
        model.addAttribute("usuario", usuarioService.findById(idUsuarioLogeado));
    }

    return "listaUsuarios";
}
```

## 🔸 B. Descripción de Usuario `/registrados/{id}`

```java
@GetMapping("/registrados/{id}")
public String descripcionUsuario(@PathVariable Long id, Model model) {
    UsuarioData usuario = usuarioService.getUsuarioDataPublic(id);
    if (usuario == null) {
        throw new UsuarioNotFoundException();
    }
    model.addAttribute("usuarioDesc", usuario);

    Long idLogeado = managerUserSession.usuarioLogeado();
    if (idLogeado != null) {
        model.addAttribute("usuario", usuarioService.findById(idLogeado));
    }

    return "descripcionUsuario";
}
```
# 💼 Servicio: `UsuarioService`

**Clase:** `madstodolist.service.UsuarioService`  
**Responsabilidad:** Gestionar la lógica de negocio relacionada con los usuarios.

## 🧾 A. Obtener Todos los Usuarios

```java
@Transactional(readOnly = true)
public List<UsuarioData> findAll() {
    List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
    return usuarios.stream()
            .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
            .collect(Collectors.toList());
}
```

## 🔒 B. Obtener Datos Públicos de un Usuario

```java
@Transactional(readOnly = true)
public UsuarioData getUsuarioDataPublic(Long id) {
    Usuario usuario = usuarioRepository.findById(id).orElse(null);
    if (usuario == null) return null;

    UsuarioData usuarioData = modelMapper.map(usuario, UsuarioData.class);
    usuarioData.setPassword(null);
    return usuarioData;
}
```
# 🌐 Vistas HTML

## 🌟 A. Navbar (`fragments.html`)

```html
<div th:fragment="navbar(usuario)">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" th:href="@{/about}">ToDoList</a>

        <button class="navbar-toggler" data-bs-toggle="collapse" data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto" th:if="${usuario != null}">
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/usuarios/{id}/tareas(id=${usuario.id})}">
                        <i class="fas fa-tasks"></i> Tareas
                    </a>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item dropdown" th:if="${usuario != null}">
                    <a class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                        <i class="fas fa-user-circle"></i>
                        <span th:text="${usuario.nombre}"></span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" th:href="@{/usuarios/{id}/cuenta(id=${usuario.id})}">Mi Cuenta</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" th:href="@{/logout}">Cerrar Sesión</a></li>
                    </ul>
                </li>
                <li class="nav-item" th:unless="${usuario != null}">
                    <a class="nav-link" th:href="@{/login}">Login</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
</div>
```

## 📋 B. Listado de Usuarios (`listaUsuarios.html`)

```html
<table class="table">
<thead>
<tr><th>ID</th><th>Email</th></tr>
</thead>
<tbody>
<tr th:each="usuario : ${usuarios}">
<td th:text="${usuario.id}"></td>
<td>
<a th:href="@{/registrados/{id}(id=${usuario.id})}" th:text="${usuario.email}"></a>
</td>
</tr>
</tbody>
</table>
```

## 🧾 C. Descripción de Usuario (`descripcionUsuario.html`)

```html
<div class="card">
<div class="card-body">
<h5 th:text="${usuarioDesc.nombre}"></h5>
<p><strong>Email:</strong> <span th:text="${usuarioDesc.email}"></span></p>
<p><strong>Fecha Nacimiento:</strong> 
<span th:text="${#dates.format(usuarioDesc.fechaNacimiento, 'dd/MM/yyyy')}"></span>
</p>
<a th:href="@{/registrados}" class="btn btn-primary">Volver</a>
</div>
</div>
```
