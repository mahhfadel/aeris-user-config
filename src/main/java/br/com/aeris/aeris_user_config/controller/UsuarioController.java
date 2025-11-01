package br.com.aeris.aeris_user_config.controller;

import br.com.aeris.aeris_user_config.dto.AllUsuariosResponse;
import br.com.aeris.aeris_user_config.dto.ErrorResponse;
import br.com.aeris.aeris_user_config.dto.UsuarioRequest;
import br.com.aeris.aeris_user_config.dto.UsuarioResponse;
import br.com.aeris.aeris_user_config.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/criar-usuario")
    @Operation(summary = "Criar usuário", description = "Endpoint para criar um novo usuário")
    public ResponseEntity<UsuarioResponse> createUsuario(@RequestBody UsuarioRequest request) {
        try {
            UsuarioResponse response = usuarioService.createUsuario(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(UsuarioResponse.builder().mensagem(e.getMessage()).build());
        }
    }

    @GetMapping("/allUsuario")
    @Operation(summary = "Retornar todos usuários", description = "Endpoint para retornar todos os usuários")
    public ResponseEntity<?> getAllColaboraderesUsers(@RequestParam Long empresa) {
        try {
            List<AllUsuariosResponse> response = usuarioService.getAllColaboraderesUsers(empresa);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .path("/api/usuarios/allUsuario")
                    .build();

            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/retornar-usuario")
    @Operation(summary = "Retornar Usuário", description = "Endpoint para retornar um usuário")
    public ResponseEntity<UsuarioResponse> readUsuario(@RequestParam String email) {
        try {
            UsuarioResponse response = usuarioService.readUsuario(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(UsuarioResponse.builder().mensagem(e.getMessage()).build());
        }
    }

}
