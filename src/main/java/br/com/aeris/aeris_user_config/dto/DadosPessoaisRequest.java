package br.com.aeris.aeris_user_config.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DadosPessoaisRequest {
    private String genero;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contratadoEm;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;
    private String setor;
    private String cargo;
    private String sexualidade;
    private boolean termosDeUso;
    private String emailUsuario;
}
