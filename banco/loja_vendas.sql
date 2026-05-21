CREATE DATABASE IF NOT EXISTS loja_vendas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;


USE loja_vendas;



CREATE TABLE IF NOT EXISTS clientes (

    id INT NOT NULL AUTO_INCREMENT,
    nome  VARCHAR(100)  NOT NULL,
    email VARCHAR(150)  NOT NULL,

    CONSTRAINT pk_clientes   PRIMARY KEY (id),
    -- UNIQUE: garante que dois clientes não podem ter o mesmo
    CONSTRAINT uq_cli_email  UNIQUE (email),
    -- CHECK: validações extras. TRIM remove espaços em branco para garantir que o campo não seja salvo só com espaços.
    CONSTRAINT ck_cli_nome   CHECK (TRIM(nome)  <> ''),
    CONSTRAINT ck_cli_email  CHECK (TRIM(email) <> '')

) 

CREATE TABLE IF NOT EXISTS produtos (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    estoque INT NOT NULL DEFAULT 0,
    categoria ENUM('ALIMENTOS', 'ELETRONICOS', 'LIVROS') NOT NULL,

    CONSTRAINT pk_produtos PRIMARY KEY (id),
    -- Garante que o preço seja positivo e não absurdamente alto (adicionei isso por conta do que o professor falou na ultima aula, onde ele ira testar tudo)
    CONSTRAINT ck_prod_preco CHECK (preco   >  0 AND preco   <= 999999.99),
    -- Garante que o estoque nunca fique negativo no banco.   
    CONSTRAINT ck_prod_estoque CHECK (estoque >= 0 AND estoque <= 999999),
    CONSTRAINT ck_prod_nome CHECK (TRIM(nome) <> '')

) 

CREATE TABLE IF NOT EXISTS pedidos (
    id INT NOT NULL AUTO_INCREMENT,
    cliente_id INT NOT NULL,

    status ENUM('ABERTO','FILA','PROCESSANDO','FINALIZADO')  NOT NULL DEFAULT 'ABERTO',

    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_pedidos PRIMARY KEY (id),
    CONSTRAINT fk_ped_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE

) 

CREATE TABLE IF NOT EXISTS itens_pedido (
    id INT NOT NULL AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,

    CONSTRAINT pk_itens PRIMARY KEY (id), 
    CONSTRAINT fk_item_pedido  FOREIGN KEY (pedido_id)
        REFERENCES pedidos (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id)
        REFERENCES produtos (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    -- garante que o estoque nao seja negativo 
    CONSTRAINT ck_item_qtd CHECK (quantidade >  0 AND quantidade <= 999999),

    -- Garante que o preço registrado seja positivo.
    CONSTRAINT ck_item_preco_unit CHECK (preco_unitario >  0 AND preco_unitario <= 999999.99)

) 