package br.com.aeris.aeris_user_config.controller;

import br.com.aeris.aeris_user_config.dto.UsuarioRequest;
import br.com.aeris.aeris_user_config.dto.UsuarioResponse;
import br.com.aeris.aeris_user_config.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
