package br.com.aeris.aeris_user_config.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo_pergunta")
public class TipoPergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;


    @ManyToOne
    @JoinColumn(name = "tipo_pergunta_id")
    private Pergunta pergunta;
}
