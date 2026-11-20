CREATE TABLE ocorrencias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    endereco VARCHAR(200),
    status VARCHAR(30) NOT NULL,
    mensagem_administrador VARCHAR(1000),
    usuario_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_ocorrencias_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);