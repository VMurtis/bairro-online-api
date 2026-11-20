
package br.com.fiap.api.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class ArmazenamentoFotoService {

    private final Path diretorio = Paths.get("uploads")
            .toAbsolutePath()
            .normalize();

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public String salvar(MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) {
            return null;
        }

        if (!TIPOS_PERMITIDOS.contains(foto.getContentType())) {
            throw new IllegalArgumentException(
                    "Envie uma imagem JPG, PNG ou WEBP."
            );
        }

        Files.createDirectories(diretorio);

        String nomeOriginal = StringUtils.cleanPath(
                foto.getOriginalFilename() == null
                        ? ""
                        : foto.getOriginalFilename()
        );

        String extensao = StringUtils.getFilenameExtension(nomeOriginal);

        String nomeArquivo = UUID.randomUUID()
                + (extensao == null ? "" : "." + extensao.toLowerCase());

        Path destino = diretorio.resolve(nomeArquivo).normalize();

        if (!destino.startsWith(diretorio)) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        foto.transferTo(destino);

        return "/uploads/" + nomeArquivo;
    }


    public void excluir(String fotoUrl) throws IOException {
        if (fotoUrl == null || fotoUrl.isBlank()) {
            return;
        }

        String prefixo = "/uploads/";

        if (!fotoUrl.startsWith(prefixo)) {
            throw new IllegalArgumentException("Caminho de foto inválido.");
        }

        String nomeArquivo = fotoUrl.substring(prefixo.length());

        if (nomeArquivo.isBlank() || nomeArquivo.contains("/") || nomeArquivo.contains("\\")) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        Path arquivo = diretorio.resolve(nomeArquivo).normalize();

        if (!arquivo.startsWith(diretorio)) {
            throw new IllegalArgumentException("Caminho de arquivo inválido.");
        }

        Files.deleteIfExists(arquivo);
    }

}
