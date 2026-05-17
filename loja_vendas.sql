create database loja_vendas
character set utf8mb4
collate utf8mb4_general_ci;

use loja_vendas;

create table cliente(
	id_cliente int primary key auto_increment,
    nome varchar(150) not null,
    email varchar(100) not null


);

create table produto(
	id_produto int primary key auto_increment,
    nome varchar(100) not null,
    preco decimal(10, 2) not null,
    estoque int not null,
    categoria varchar(50)


);