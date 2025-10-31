package br.com.aeris.aeris_user_config.dto;

import br.com.aeris.aeris_user_config.model.DadosPessoais;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {
    private String email;
    private String nome;
    private String sobrenome;
    private String tipo;
    private DadosPessoais dadosPessoais;
    private String mensagem;
}
