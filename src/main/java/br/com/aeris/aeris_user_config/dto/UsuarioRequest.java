package br.com.aeris.aeris_user_config.dto;

import br.com.aeris.aeris_user_config.model.Empresa;
import lombok.Data;

@Data
public class UsuarioRequest {
    private String email;
    private String nome;
    private String sobrenome;
    private Long empresa;
}
