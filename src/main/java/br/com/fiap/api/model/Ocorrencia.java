package br.com.fiap.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocorrencias")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String categoria;

    @Column(length = 1000, nullable = false)
    private String descricao;

    @Column(length = 200)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOcorrencia status = StatusOcorrencia.PENDENTE;

    @Column(length = 1000)
    private String mensagemAdministrador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private LocalDateTime dataRegistro;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;
    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
    @PrePersist
    public void registrarData() {
        if (dataRegistro == null) {
            dataRegistro = LocalDateTime.now();
        }
    }


    public void atualizarDados(
            br.com.fiap.api.dto.DadosAtualizacaoOcorrencia dados
    ) {
        this.titulo = dados.titulo();
        this.categoria = dados.categoria();
        this.descricao = dados.descricao();
        this.endereco = dados.endereco();
    }


    public void atualizarStatus(StatusOcorrencia novoStatus) {
        this.status = novoStatus;
    }

    public void atualizarMensagemAdministrador(String mensagem) {
        this.mensagemAdministrador = mensagem;
    }

}