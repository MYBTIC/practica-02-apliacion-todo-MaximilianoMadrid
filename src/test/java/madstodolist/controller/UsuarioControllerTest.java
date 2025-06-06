package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void accesoCuentaUsuarioLogeado() throws Exception {
        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);
        usuario.setNombre("Usuario Prueba");
        usuario.setEmail("test@test.com");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/1/cuenta"))
                .andExpect(status().isOk())
                .andExpect(view().name("formCuentaUsuario"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("usuario", usuario));
    }

    @Test
    public void accesoCuentaUsuarioNoLogeado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/usuarios/1/cuenta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void accesoCuentaOtroUsuario() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(2L);

        mockMvc.perform(get("/usuarios/1/cuenta"))
                .andExpect(status().isUnauthorized());
    }
}