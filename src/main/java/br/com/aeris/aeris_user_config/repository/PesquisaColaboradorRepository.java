package br.com.aeris.aeris_user_config.repository;

import br.com.aeris.aeris_user_config.model.Pesquisa;
import br.com.aeris.aeris_user_config.model.PesquisaColaborador;
import br.com.aeris.aeris_user_config.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PesquisaColaboradorRepository extends JpaRepository<PesquisaColaborador, Long> {
    List<PesquisaColaborador> findByUsuario(Usuario usuario);

    List<PesquisaColaborador> findByPesquisa(Pesquisa pesquisa);

    Optional<PesquisaColaborador> findByToken(String token);
}
