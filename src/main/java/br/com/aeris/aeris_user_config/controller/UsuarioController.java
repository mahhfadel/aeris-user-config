package br.com.aeris.aeris_user_config.controller;

import br.com.aeris.aeris_user_config.dto.AllUsuariosResponse;
import br.com.aeris.aeris_user_config.dto.ErrorResponse;
import br.com.aeris.aeris_user_config.dto.UsuarioRequest;
import br.com.aeris.aeris_user_config.dto.UsuarioResponse;
import br.com.aeris.aeris_user_config.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity<?> createUsuario(@RequestBody UsuarioRequest request) {
        try {
            UsuarioResponse response = usuarioService.createUsuario(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Erro de validação (400)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())

                    .build();
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            // Erro genérico (500)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro interno no servidor")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/allUsuario")
    @Operation(summary = "Retornar todos usuários", description = "Endpoint para retornar todos os usuários")
    public ResponseEntity<?> getAllColaboraderesUsers(@RequestParam Long empresa, @RequestParam(required = false) Long idPesquisa) {
        try {
            List<AllUsuariosResponse> response = usuarioService.getAllColaboradoresUsers(empresa, idPesquisa);
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
    public ResponseEntity<?> readUsuario(@RequestParam String email) {
        try {
            UsuarioResponse response = usuarioService.readUsuario(email);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            // Não encontrado (404)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception e) {
            // Erro genérico (500)
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Erro interno no servidor")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
