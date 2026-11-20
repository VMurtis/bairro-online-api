package br.com.fiap.api.controller;

import br.com.fiap.api.dto.DadosAtualizacaoOcorrencia;
import br.com.fiap.api.dto.DadosCadastroOcorrencia;
import br.com.fiap.api.dto.DadosListagemOcorrencia;
import br.com.fiap.api.model.Ocorrencia;
import br.com.fiap.api.model.StatusOcorrencia;
import br.com.fiap.api.model.Usuario;
import br.com.fiap.api.repository.OcorrenciaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.api.service.ArmazenamentoFotoService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.http.MediaType;

import java.io.IOException;



@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaRepository repository;

    private final ArmazenamentoFotoService armazenamentoFotoService;

    public OcorrenciaController(
            OcorrenciaRepository repository,
            ArmazenamentoFotoService armazenamentoFotoService
    ) {
        this.repository = repository;
        this.armazenamentoFotoService = armazenamentoFotoService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosListagemOcorrencia> cadastrar(
            @RequestPart("dados") @Valid DadosCadastroOcorrencia dados,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @AuthenticationPrincipal Usuario usuario
    ) throws IOException {

        String fotoUrl;

        try {
            fotoUrl = armazenamentoFotoService.salvar(foto);
        } catch (IllegalArgumentException erro) {
            return ResponseEntity.badRequest().build();
        }

        Ocorrencia ocorrencia = new Ocorrencia(
                null,
                dados.titulo(),
                dados.categoria(),
                dados.descricao(),
                dados.endereco(),
                StatusOcorrencia.PENDENTE,
                null,
                usuario,
                null,
                fotoUrl
        );

        Ocorrencia ocorrenciaSalva = repository.save(ocorrencia);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DadosListagemOcorrencia(ocorrenciaSalva));
    }

    @GetMapping("/minhas")
    public List<DadosListagemOcorrencia> listarMinhas(
            @AuthenticationPrincipal Usuario usuario
    ) {
        return repository.findByUsuarioId(usuario.getId())
                .stream()
                .map(DadosListagemOcorrencia::new)
                .toList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<DadosListagemOcorrencia> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return repository.findByIdAndUsuarioId(id, usuario.getId())
                .map(ocorrencia -> ResponseEntity.ok(
                        new DadosListagemOcorrencia(ocorrencia)
                ))
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosListagemOcorrencia> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoOcorrencia dados,
            @AuthenticationPrincipal Usuario usuario
    ) {
        var resultado = repository.findByIdAndUsuarioId(id, usuario.getId());

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ocorrencia ocorrencia = resultado.get();
        ocorrencia.atualizarDados(dados);

        return ResponseEntity.ok(new DadosListagemOcorrencia(ocorrencia));
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        var resultado = repository.findByIdAndUsuarioId(id, usuario.getId());

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        repository.delete(resultado.get());

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<DadosListagemOcorrencia> atualizarFoto(
            @PathVariable Long id,
            @RequestPart("foto") MultipartFile foto,
            @AuthenticationPrincipal Usuario usuario
    ) throws IOException {

        if (foto == null || foto.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var resultado = repository.findByIdAndUsuarioId(id, usuario.getId());

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ocorrencia ocorrencia = resultado.get();
        String fotoAntiga = ocorrencia.getFotoUrl();
        String fotoNova;

        try {
            fotoNova = armazenamentoFotoService.salvar(foto);
        } catch (IllegalArgumentException erro) {
            return ResponseEntity.badRequest().build();
        }

        ocorrencia.setFotoUrl(fotoNova);
        repository.saveAndFlush(ocorrencia);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        try {
                            armazenamentoFotoService.excluir(fotoAntiga);
                        } catch (IOException erro) {
                            java.util.logging.Logger
                                    .getLogger(OcorrenciaController.class.getName())
                                    .log(
                                            java.util.logging.Level.SEVERE,
                                            "Não foi possível excluir a foto antiga.",
                                            erro
                                    );
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                            try {
                                armazenamentoFotoService.excluir(fotoNova);
                            } catch (IOException erro) {
                                java.util.logging.Logger
                                        .getLogger(OcorrenciaController.class.getName())
                                        .log(
                                                java.util.logging.Level.SEVERE,
                                                "Não foi possível excluir a nova foto após falha na transação.",
                                                erro
                                        );
                            }
                        }
                    }
                }
        );

        return ResponseEntity.ok(new DadosListagemOcorrencia(ocorrencia));
    }


}