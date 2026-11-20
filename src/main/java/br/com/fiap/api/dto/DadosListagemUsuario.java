package br.com.fiap.api.dto;

import br.com.fiap.api.model.Usuario;


public record DadosListagemUsuario(
        Long id,
        String nome,
        String email,
        String perfil
) {

    public DadosListagemUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }
}
