# Sistema de Gestão de Pedidos

Projeto feito para a faculdade, um sistema de vendas no terminal usando Java e MySQL. Da pra cadastrar clientes, produtos, fazer pedidos e ver relatorios.

---

## O que precisa ter instalado

- Java 17+
- MySQL 8+
- O driver JDBC do MySQL ja na pasta `lib/`

---

## Antes de rodar

Abre o MySQL e executa o script do banco:

```sql
source banco/loja_vendas.sql
```

Depois confere se o usuario e senha do MySQL estão certos no arquivo `utilitarios/Conexao.java`. Por padrão ta como root sem senha, se o seu for diferente é só trocar lá.

```java
private static final String USUARIO = "root";
private static final String SENHA   = "";
```

---

## Como compilar e rodar

Compila tudo com esse comando na raiz do projeto:

```bash
javac -cp "lib/*" -d . Main.java modelos/*.java utilitarios/*.java dados/*.java telas/*.java RegraNegocioException.java
```

Pra rodar:

```bash
java -cp ".;lib/*" Main
```

> No Linux/Mac troca `;` por `:` no classpath.

---

## Como usar

Quando abre o sistema aparece o menu principal:

```
--- MENU PRINCIPAL ---
1. Clientes
2. Produtos
3. Pedidos
4. Relatorios
0. Sair
```

### Clientes

 Opção  O que faz 
------------------
 1      Cadastrar novo cliente 
 2      Listar todos os clientes 
 3      Buscar cliente por ID 

---

### Produtos

 Opção  O que faz 
------------------
 1      Cadastrar novo produto 
 2      Listar todos os produtos 
 3      Buscar produto por ID 

Categorias disponíveis: `ALIMENTOS`, `ELETRONICOS`, `LIVROS`

---

### Pedidos

 Opção  O que faz 
------------------
 1      Criar novo pedido 
 2      Listar todos os pedidos 
 3      Detalhar pedido por ID 

Pra criar um pedido: informa o ID do cliente, depois vai escolhendo os produtos e quantidades. Digita `0` no ID do produto quando terminar. O estoque é descontado automaticamente e o pedido entra na fila pra ser processado em segundo plano.

**Status do pedido:**

 Status        / Descrição 
---------------/-----------
 `FILA`        / Aguardando processamento 
 `PROCESSANDO` / Sendo processado 
 `FINALIZADO`  / Concluído 


### Relatorios

 Opção / O que faz 
-------/-----------
 1     / Produtos mais vendidos 
 2     / Vendas por categoria (valor em R$) 
 3     / Pedidos por status (quantidade e valor total) 

---


## Equipe

- Leo - telas e menus
- João - validações e processamento de pedidos
- Luis - repositórios e serviços do banco de dados
