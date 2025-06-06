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

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void aboutNoLogeado() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attributeDoesNotExist("usuario"));
    }

    @Test
    public void aboutLogeado() throws Exception {
        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);
        usuario.setNombre("Usuario de prueba");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(usuario);

        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("usuario", usuario));
    }
}