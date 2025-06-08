package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    @GetMapping("/usuarios/{id}/cuenta")
    public String formEditaCuenta(@PathVariable(value = "id") Long idUsuario, Model model) {
        comprobarUsuarioLogeado(idUsuario);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        return "formCuentaUsuario";
    }

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model) {
        List<UsuarioData> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);

        // Añadimos también el usuario logeado para la navbar
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuario);
        }

        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable(value = "id") Long idUsuario, Model model) {
        // Obtenemos los datos del usuario a mostrar
        UsuarioData usuarioMostrado = usuarioService.findById(idUsuario);
        if (usuarioMostrado == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuarioMostrado", usuarioMostrado);

        // Añadimos también el usuario logeado (para la navbar)
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuarioLogeado);
        }

        return "descripcionUsuario";
    }

}