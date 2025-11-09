package br.com.aeris.aeris_user_config.service;

import br.com.aeris.aeris_user_config.dto.DadosPessoaisRequest;
import br.com.aeris.aeris_user_config.dto.DadosPessoaisResponse;
import br.com.aeris.aeris_user_config.model.DadosPessoais;
import br.com.aeris.aeris_user_config.repository.DadosPessoaisRepository;
import br.com.aeris.aeris_user_config.repository.EmpresaRepository;
import br.com.aeris.aeris_user_config.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DadosPessoaisService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DadosPessoaisRepository dadosPessoaisRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private static final Logger logger = LoggerFactory.getLogger(DadosPessoaisService.class);

    @Transactional
    public DadosPessoaisResponse responderSenso(DadosPessoaisRequest request) {
        logger.info("[SensoService.responderSenso] Iniciando resposta do senso para o usuário com email: {}", request.getEmailUsuario());

        if (!usuarioRepository.existsByEmail(request.getEmailUsuario())) {
            logger.warn("[SensoService.responderSenso] Tentativa de resposta com email inexistente: {}", request.getEmailUsuario());
            throw new RuntimeException("Não existe um usuário com esse email");
        }

        if (dadosPessoaisRepository.existsByUsuario(usuarioRepository.findByEmail(request.getEmailUsuario()))) {
            logger.warn("[SensoService.responderSenso] Usuário com email {} já respondeu o senso", request.getEmailUsuario());
            throw new IllegalArgumentException("Usuário já respondeu o senso");
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

        logger.info("[SensoService.responderSenso] Salvando dados pessoais do usuário: {}", request.getEmailUsuario());
        dadosPessoaisRepository.save(dadosPessoais);
        logger.info("[SensoService.responderSenso] Dados pessoais salvos com sucesso para o usuário: {}", request.getEmailUsuario());

        return DadosPessoaisResponse.builder()
                .emailUsuario(request.getEmailUsuario())
                .dadosRespondidos(true)
                .mensagem("Senso respondido com sucesso")
                .build();
    }

    public DadosPessoaisResponse respondeuOSenso(String email) {
        logger.info("[SensoService.respondeuOSenso] Verificando se o usuário respondeu o senso: {}", email);

        if (!usuarioRepository.existsByEmail(email)) {
            logger.warn("[SensoService.respondeuOSenso] Verificação falhou: não existe usuário com email {}", email);
            throw new RuntimeException("Não existe um usuário com esse email");
        }

        boolean respondeu = dadosPessoaisRepository.existsByUsuario(usuarioRepository.findByEmail(email));

        if (respondeu) {
            logger.info("[SensoService.respondeuOSenso] Usuário {} já respondeu o senso", email);
            return DadosPessoaisResponse.builder()
                    .dadosRespondidos(true)
                    .mensagem("Usuário já respondeu o senso")
                    .build();
        } else {
            logger.info("[SensoService.respondeuOSenso] Usuário {} ainda não respondeu o senso", email);
            return DadosPessoaisResponse.builder()
                    .dadosRespondidos(false)
                    .mensagem("Usuário não respondeu o senso")
                    .build();
        }
    }

}
