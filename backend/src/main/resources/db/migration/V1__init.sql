-- Estrutura inicial do stock-management
-- Cada tabela representa uma peça do fluxo: insumos -> receita dos produtos ->
-- movimentações de estoque -> embalagens -> envios de volta para a fábrica.

CREATE TABLE insumos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    unidade_medida VARCHAR(30),
    quantidade_estoque INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_insumo_nome UNIQUE (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    insumo_gargalo_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_produto_nome UNIQUE (nome),
    CONSTRAINT fk_produto_gargalo FOREIGN KEY (insumo_gargalo_id) REFERENCES insumos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE produto_insumos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    insumo_id BIGINT NOT NULL,
    quantidade_necessaria INT NOT NULL,
    CONSTRAINT fk_pi_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE,
    CONSTRAINT fk_pi_insumo FOREIGN KEY (insumo_id) REFERENCES insumos(id),
    CONSTRAINT uq_produto_insumo UNIQUE (produto_id, insumo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE movimentacoes_estoque (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    insumo_id BIGINT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    motivo VARCHAR(255),
    data_movimentacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_insumo FOREIGN KEY (insumo_id) REFERENCES insumos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE envios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE embalagens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    data_embalagem TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    envio_id BIGINT,
    CONSTRAINT fk_emb_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    CONSTRAINT fk_emb_envio FOREIGN KEY (envio_id) REFERENCES envios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_mov_insumo ON movimentacoes_estoque(insumo_id);
CREATE INDEX idx_emb_produto ON embalagens(produto_id);
CREATE INDEX idx_emb_envio ON embalagens(envio_id);
