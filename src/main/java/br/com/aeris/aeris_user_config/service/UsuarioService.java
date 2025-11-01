package br.com.aeris.aeris_user_config.service;

import br.com.aeris.aeris_user_config.dto.AllUsuariosResponse;
import br.com.aeris.aeris_user_config.dto.UsuarioRequest;
import br.com.aeris.aeris_user_config.dto.UsuarioResponse;
import br.com.aeris.aeris_user_config.model.DadosPessoais;
import br.com.aeris.aeris_user_config.model.Usuario;
import br.com.aeris.aeris_user_config.repository.DadosPessoaisRepository;
import br.com.aeris.aeris_user_config.repository.EmpresaRepository;
import br.com.aeris.aeris_user_config.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DadosPessoaisRepository dadosPessoaisRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public UsuarioResponse createUsuario(UsuarioRequest request){

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Erro: Já existe um usuário com o email " + request.getEmail());
        }

        if (!emailValidator.isValid(request.getEmail())) {
            throw new IllegalArgumentException("Email inválido: " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setSobrenome(request.getSobrenome());
        usuario.setEmail(request.getEmail());
        usuario.setTipo("colaborador");
        usuario.setEmpresa(empresaRepository.getReferenceById(request.getEmpresa()));
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .tipo(usuario.getTipo())
                .mensagem("Usuário criado com sucesso")
                .build();
    }

    public List<AllUsuariosResponse> getAllColaboraderesUsers(Long empresa){
        List<Usuario> usuarios = usuarioRepository.findByEmpresa(
                empresaRepository.getReferenceById(empresa)).stream()
                .filter(u-> Objects.equals(u.getTipo(), "colaborador"))
                .toList();

        List<AllUsuariosResponse> responses = new ArrayList<>();

        for(Usuario user: usuarios){
            DadosPessoais dadosPessoais = dadosPessoaisRepository.findByUsuario(user);

            AllUsuariosResponse response = AllUsuariosResponse.builder()
                    .id(user.getId())
                    .nome(user.getNome().concat(" ").concat(user.getSobrenome()))
                    .genero(dadosPessoais.getGenero())
                    .setor(dadosPessoais.getSetor())
                    .cargo(dadosPessoais.getCargo())
                    .tempoDeCasa(formatarPeriodo(dadosPessoais.getContratado_em()))
                    .build();

            responses.add(response);
        }

        return responses;
    }

    public static String formatarPeriodo(LocalDateTime data) {
        if (data == null) {
            return "Data não informada";
        }

        LocalDateTime agora = LocalDateTime.now();

        if (data.isAfter(agora)) {
            return "Data no futuro";
        }

        Period periodo = Period.between(
                data.toLocalDate(),
                agora.toLocalDate()
        );

        int anos = periodo.getYears();
        int meses = periodo.getMonths();

        StringBuilder resultado = new StringBuilder();

        if (anos > 0) {
            resultado.append(anos).append(anos == 1 ? " ano" : " anos");
        }

        if (resultado.length() > 0) {
            resultado.append(" e ");
        }

        if(meses > 0) {
            resultado.append(meses).append(meses == 1 ? " mês" : " meses");
        } else {
            resultado.append("1 mês");
        }

        return resultado.toString();
    }

    public UsuarioResponse readUsuario(String email){
        Usuario usuario = usuarioRepository.findByEmail(email);

        if(usuario == null){
            throw new IllegalArgumentException("Não existe um usuário com esse email");
        }

        DadosPessoais dadosPessoais = new DadosPessoais();
        if (dadosPessoaisRepository.existsByUsuario(usuario)){
            dadosPessoais = dadosPessoaisRepository.findByUsuario(usuario);
        }

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
