package br.com.fiap.api.controller;

import br.com.fiap.api.dto.DadosAtualizacaoPerfil;
import br.com.fiap.api.dto.DadosAtualizacaoUsuario;
import br.com.fiap.api.dto.DadosListagemUsuario;
import br.com.fiap.api.model.Usuario;
import br.com.fiap.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import br.com.fiap.api.dto.DadosCadastroUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;


@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        String senhaCodificada = codificadorSenha.encode(dados.senha());

        Usuario usuario = new Usuario(
                null,
                dados.nome(),
                dados.email(),
                senhaCodificada,
                "CIDADAO");

        repository.save(usuario);
    }
    @GetMapping
    public List<DadosListagemUsuario> listar() {
        return repository.findAll().stream().map(DadosListagemUsuario::new).toList();
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        var usuario = repository.getReferenceById(dados.id());
        usuario.atualizarInformacoes(dados);
    }

    @DeleteMapping("/perfil")
    @Transactional
    public ResponseEntity<Void> excluirPerfil(
            @AuthenticationPrincipal Usuario usuario
    ) {
        Usuario usuarioBanco = repository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        repository.delete(usuarioBanco);

        return ResponseEntity.noContent().build();
    }


    private final PasswordEncoder codificadorSenha;

    public UsuarioController(
            UsuarioRepository repository,
            PasswordEncoder codificadorSenha
    ) {
        this.repository = repository;
        this.codificadorSenha = codificadorSenha;
    }

    @GetMapping("/perfil")
    public DadosListagemUsuario perfil(
            @AuthenticationPrincipal Usuario usuario
    ) {
        return new DadosListagemUsuario(usuario);
    }

    @PutMapping("/perfil")
    @Transactional
    public ResponseEntity<DadosListagemUsuario> atualizarPerfil(
            @RequestBody @Valid DadosAtualizacaoPerfil dados,
            @AuthenticationPrincipal Usuario usuario
    ) {
        Usuario usuarioBanco = repository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioBanco.atualizarPerfil(dados);

        return ResponseEntity.ok(new DadosListagemUsuario(usuarioBanco));
    }
}
