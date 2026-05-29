# Sistema de Gestão para Advogada Autônoma

Sistema web desenvolvido para substituir o uso de planilhas no controle da rotina jurídica, oferecendo uma solução prática e de baixo custo para gerenciamento de clientes, processos e financeiro.

## Motivação

Desenvolvido sob medida para uma advogada autônoma, com o objetivo de digitalizar e centralizar informações que antes eram controladas manualmente em planilhas, sem depender de sistemas robustos de gestão de escritório — que costumam ter custos elevados.

## Funcionalidades

- Cadastro, busca, atualização e exclusão de clientes, processos e registros financeiros (CRUD)
- Gerenciamento de pagamentos e parcelas
- Acompanhamento de valores pagos e pendentes
- Relacionamento entre clientes, processos e movimentações financeiras
- Acompanhamento de status de processos

## Boas Práticas Aplicadas

- Arquitetura em camadas (controller, service, repository, DTO e entities)
- Tratamento de exceções personalizado com respostas claras da API
- API REST para comunicação entre as camadas
- Código organizado para manutenção e escalabilidade

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security + OAuth2 / JWT
- Banco de dados Postgresql
- Frontend estático (HTML, CSS e JS)

## Implantação

A aplicação está em uso real, implantada localmente na máquina da cliente. O serviço Spring roda via **NSSM** (Non-Sucking Service Manager) e o banco de dados opera localmente — solução escolhida para eliminar custos de infraestrutura em nuvem.

Em produção. Novas funcionalidades e melhorias são implementadas regularmente.


