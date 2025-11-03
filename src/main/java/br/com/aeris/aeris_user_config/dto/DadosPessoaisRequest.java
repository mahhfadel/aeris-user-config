package br.com.aeris.aeris_user_config.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DadosPessoaisRequest {
    private String genero;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contratado_em;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data_nascimento;
    private String setor;
    private String cargo;
    private String sexualidade;
    private boolean termos_de_uso;
    private String emailUsuario;
}
