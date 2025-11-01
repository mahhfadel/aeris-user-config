package br.com.aeris.aeris_user_config.controller;


import br.com.aeris.aeris_user_config.dto.DadosPessoaisRequest;
import br.com.aeris.aeris_user_config.dto.DadosPessoaisResponse;
import br.com.aeris.aeris_user_config.dto.UsuarioResponse;
import br.com.aeris.aeris_user_config.service.DadosPessoaisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/senso")
@Tag(name = "Senso", description = "Endpoints para gerenciamento dos dados pessoais dos usuários")
public class DadosPessoaisController {
    @Autowired
    private DadosPessoaisService dadosPessoaisService;

    @PostMapping("/responder-senso")
    @Operation(summary = "Responder senso", description = "Endpoint para responder o senso")
    public ResponseEntity<DadosPessoaisResponse> createSenso(@RequestBody DadosPessoaisRequest request) {
        try {
            DadosPessoaisResponse response = dadosPessoaisService.responderSenso(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(DadosPessoaisResponse.builder()
                            .dadosRespondidos(null)
                            .mensagem(e.getMessage()).build());
        }
    }

    @GetMapping("/respondeu")
    @Operation(summary = "Usuário respondeu o senso", description = "Endpoint para retornar se um usuário ja respondeu o senso")
    public ResponseEntity<DadosPessoaisResponse> respondeuOSenso(@RequestParam String email) {
        try {
            DadosPessoaisResponse response = dadosPessoaisService.respondeuOSenso(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(DadosPessoaisResponse.builder()
                            .dadosRespondidos(null)
                            .mensagem(e.getMessage()).build());
        }
    }
}
