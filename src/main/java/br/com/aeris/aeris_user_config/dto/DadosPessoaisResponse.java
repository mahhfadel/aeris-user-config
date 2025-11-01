package br.com.aeris.aeris_user_config.dto;

import br.com.aeris.aeris_user_config.model.DadosPessoais;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DadosPessoaisResponse {
    private String emailUsuario;
    private Boolean dadosRespondidos;
    private String mensagem;
}
