package br.com.fiap.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoPerfil(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email
) {
}