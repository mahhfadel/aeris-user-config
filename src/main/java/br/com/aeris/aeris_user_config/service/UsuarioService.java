package br.com.aeris.aeris_user_config.service;

import br.com.aeris.aeris_user_config.dto.AllUsuariosResponse;
import br.com.aeris.aeris_user_config.dto.UsuarioRequest;
import br.com.aeris.aeris_user_config.dto.UsuarioResponse;
import br.com.aeris.aeris_user_config.model.DadosPessoais;
import br.com.aeris.aeris_user_config.model.PesquisaColaborador;
import br.com.aeris.aeris_user_config.model.Usuario;
import br.com.aeris.aeris_user_config.repository.DadosPessoaisRepository;
import br.com.aeris.aeris_user_config.repository.EmpresaRepository;
import br.com.aeris.aeris_user_config.repository.PesquisaColaboradorRepository;
import br.com.aeris.aeris_user_config.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DadosPessoaisRepository dadosPessoaisRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PesquisaColaboradorRepository pesquisaColaboradorRepository;

    private final EmailService emailService;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public UsuarioService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Transactional
    public UsuarioResponse createUsuario(UsuarioRequest request) {
        logger.info("[UsuarioService.createUsuario] Iniciando criação de usuário com email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            logger.warn("[UsuarioService.createUsuario] Já existe um usuário com o email {}", request.getEmail());
            throw new IllegalArgumentException("Já existe um usuário com o email " + request.getEmail());
        }

        if (!emailValidator.isValid(request.getEmail())) {
            logger.warn("[UsuarioService.createUsuario] Email inválido fornecido: {}", request.getEmail());
            throw new IllegalArgumentException("Email inválido!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setSobrenome(request.getSobrenome());
        usuario.setEmail(request.getEmail());
        usuario.setTipo("colaborador");
        usuario.setEmpresa(empresaRepository.getReferenceById(request.getEmpresa()));
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);
        logger.info("[UsuarioService.createUsuario] Usuário criado com sucesso: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        emailService.enviarEmailBoasVindas(
                usuario.getEmail(),
                usuario.getEmpresa().getNome(),
                usuario.getNome()
        );
        logger.info("[UsuarioService.createUsuario] Email de boas-vindas enviado para {}", usuario.getEmail());

        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .tipo(usuario.getTipo())
                .mensagem("Usuário criado com sucesso")
                .build();
    }

    public List<AllUsuariosResponse> getAllColaboradoresUsers(Long empresa, Long idPesquisa) {
        logger.info("[UsuarioService.getAllColaboradoresUsers] Buscando colaboradores da empresa ID={} com filtro de pesquisa ID={}", empresa, idPesquisa);

        List<Usuario> usuarios = usuarioRepository.findByEmpresa(
                        empresaRepository.getReferenceById(empresa)).stream()
                .filter(u -> Objects.equals(u.getTipo(), "colaborador"))
                .toList();

        logger.info("[UsuarioService.getAllColaboradoresUsers] Total de colaboradores encontrados: {}", usuarios.size());

        if (idPesquisa != null) {
            usuarios = usuarios.stream()
                    .filter(user -> {
                        boolean jaEstaNaPesquisa = pesquisaColaboradorRepository
                                .existsByUsuarioAndPesquisaId(user, idPesquisa);
                        if (jaEstaNaPesquisa) {
                            logger.debug("[UsuarioService.getAllColaboradoresUsers] Usuário ID={} já está na pesquisa ID={}", user.getId(), idPesquisa);
                        }
                        return !jaEstaNaPesquisa;
                    })
                    .toList();
            logger.info("[UsuarioService.getAllColaboradoresUsers] Total de colaboradores após filtro de pesquisa: {}", usuarios.size());
        }

        List<AllUsuariosResponse> responses = new ArrayList<>();

        for (Usuario user : usuarios) {
            DadosPessoais dadosPessoais = dadosPessoaisRepository.findByUsuario(user);
            if (dadosPessoais == null) {
                dadosPessoais = new DadosPessoais();
                logger.debug("[UsuarioService.getAllColaboradoresUsers] Usuário ID={} sem dados pessoais cadastrados", user.getId());
            }

            List<PesquisaColaborador> pesquisasColaborador = pesquisaColaboradorRepository.findByUsuario(user);

            AllUsuariosResponse response = AllUsuariosResponse.builder()
                    .id(user.getId())
                    .nome(user.getNome().concat(" ").concat(user.getSobrenome()))
                    .email(user.getEmail())
                    .genero(dadosPessoais.getGenero())
                    .setor(dadosPessoais.getSetor())
                    .cargo(dadosPessoais.getCargo())
                    .tempoDeCasa(formatarPeriodo(dadosPessoais.getContratadoEm()))
                    .respondidos(pesquisasColaborador.stream().filter(PesquisaColaborador::isRespondido).count())
                    .total((long) pesquisasColaborador.size())
                    .build();

            responses.add(response);
            logger.debug("[UsuarioService.getAllColaboradoresUsers] Usuário processado: ID={}, Nome={}", user.getId(), user.getNome());
        }

        responses.sort(Comparator.comparing(AllUsuariosResponse::getNome, String.CASE_INSENSITIVE_ORDER));
        logger.info("[UsuarioService.getAllColaboradoresUsers] Retornando {} colaboradores ordenados por nome", responses.size());

        return responses;
    }

    public UsuarioResponse readUsuario(String email) {
        logger.info("[UsuarioService.readUsuario] Buscando usuário pelo email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            logger.warn("[UsuarioService.readUsuario] Usuário não encontrado com email: {}", email);
            throw new EntityNotFoundException("Não existe um usuário com esse email");
        }

        logger.info("[UsuarioService.readUsuario] Usuário encontrado: ID={}, Nome={}", usuario.getId(), usuario.getNome());

        DadosPessoais dadosPessoais = new DadosPessoais();
        if (dadosPessoaisRepository.existsByUsuario(usuario)) {
            dadosPessoais = dadosPessoaisRepository.findByUsuario(usuario);
            logger.debug("[UsuarioService.readUsuario] Dados pessoais encontrados para usuário ID={}", usuario.getId());
        } else {
            logger.debug("[UsuarioService.readUsuario] Usuário ID={} não possui dados pessoais", usuario.getId());
        }

        logger.info("[UsuarioService.readUsuario] Retornando informações do usuário ID={}", usuario.getId());

        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .tipo(usuario.getTipo())
                .dadosPessoais(dadosPessoais)
                .mensagem("Usuário encontrado")
                .build();
    }


}
