package br.com.aeris.aeris_user_config.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DadosPessoaisRequest {
    private String genero;
    private LocalDateTime contratado_em;
    private LocalDateTime data_nascimento;
    private String setor;
    private String cargo;
    private String sexualidade;
    private boolean termos_de_uso;
    private String emailUsuario;
}
