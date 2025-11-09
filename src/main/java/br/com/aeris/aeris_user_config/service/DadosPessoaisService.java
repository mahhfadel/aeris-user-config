package br.com.aeris.aeris_user_config.service;

import br.com.aeris.aeris_user_config.dto.DadosPessoaisRequest;
import br.com.aeris.aeris_user_config.dto.DadosPessoaisResponse;
import br.com.aeris.aeris_user_config.model.DadosPessoais;
import br.com.aeris.aeris_user_config.model.Usuario;
import br.com.aeris.aeris_user_config.repository.DadosPessoaisRepository;
import br.com.aeris.aeris_user_config.repository.EmpresaRepository;
import br.com.aeris.aeris_user_config.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DadosPessoaisService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DadosPessoaisRepository dadosPessoaisRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Transactional
    public DadosPessoaisResponse responderSenso(DadosPessoaisRequest request){
        if(!usuarioRepository.existsByEmail(request.getEmailUsuario())){
            throw new RuntimeException("Não existe um usuário com esse email");
        }

        if(dadosPessoaisRepository.existsByUsuario(usuarioRepository.findByEmail(request.getEmailUsuario()))){
            throw new IllegalArgumentException("Usuário ja respondeu o senso");
        }

        DadosPessoais dadosPessoais = new DadosPessoais();
        dadosPessoais.setGenero(request.getGenero());
        dadosPessoais.setContratadoEm(request.getContratadoEm());
        dadosPessoais.setDataNascimento(request.getDataNascimento());
        dadosPessoais.setSetor(request.getSetor());
        dadosPessoais.setCargo(request.getCargo());
        dadosPessoais.setSexualidade(request.getSexualidade());
        dadosPessoais.setTermosDeUso(request.isTermosDeUso());
        dadosPessoais.setUsuario(usuarioRepository.findByEmail(request.getEmailUsuario()));

        dadosPessoaisRepository.save(dadosPessoais);

        return DadosPessoaisResponse.builder()
                .emailUsuario(request.getEmailUsuario())
                .dadosRespondidos(true)
                .mensagem("Senso respondido com sucesso")
                .build();
    }

    public DadosPessoaisResponse respondeuOSenso(String email){
        if(!usuarioRepository.existsByEmail(email)){
            throw new RuntimeException("Não existe um usuário com esse email");
        }

        if(dadosPessoaisRepository.existsByUsuario(usuarioRepository.findByEmail(email))){
            return DadosPessoaisResponse.builder()
                    .dadosRespondidos(true)
                    .mensagem("Usuário já respondeu o senso")
                    .build();
        } else{
            return DadosPessoaisResponse.builder()
                    .dadosRespondidos(false)
                    .mensagem("Usuário não respondeu o senso")
                    .build();
        }

    }
}
