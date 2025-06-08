package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ManagerUserSession managerUserSession;

    @Mock
    private Model model;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    public void testListadoUsuarios() {
        // Given
        UsuarioData usuarioData1 = new UsuarioData();
        usuarioData1.setId(1L);
        usuarioData1.setEmail("user1@ua");

        UsuarioData usuarioData2 = new UsuarioData();
        usuarioData2.setId(2L);
        usuarioData2.setEmail("user2@ua");

        List<UsuarioData> usuarios = Arrays.asList(usuarioData1, usuarioData2);

        when(usuarioService.findAll()).thenReturn(usuarios);
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // When
        String viewName = usuarioController.listadoUsuarios(model);

        // Then
        assertThat(viewName).isEqualTo("listaUsuarios");
        verify(usuarioService).findAll();
        verify(model).addAttribute("usuarios", usuarios);
        verify(model, never()).addAttribute(eq("usuario"), any());
    }

    @Test
    public void testListadoUsuariosConUsuarioLogeado() {
        // Given
        UsuarioData usuarioData1 = new UsuarioData();
        usuarioData1.setId(1L);

        UsuarioData usuarioLogeado = new UsuarioData();
        usuarioLogeado.setId(3L);

        when(usuarioService.findAll()).thenReturn(Arrays.asList(usuarioData1));
        when(managerUserSession.usuarioLogeado()).thenReturn(3L);
        when(usuarioService.findById(3L)).thenReturn(usuarioLogeado);

        // When
        usuarioController.listadoUsuarios(model);

        // Then
        verify(model).addAttribute("usuarios", Arrays.asList(usuarioData1));
        verify(model).addAttribute("usuario", usuarioLogeado);
    }
}