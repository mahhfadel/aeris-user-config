package br.com.aeris.aeris_user_config.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllUsuariosResponse {
    private Long id;
    private String nome;
    private String genero;
    private String setor;
    private String cargo;
    private String tempoDeCasa;
}
