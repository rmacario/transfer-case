

# Overview
Aplicação desenvolvida em java 11 e modelada/estruturada utilizando DDD. 
Na organização dos pacotes, foi utilizado o modelo `package by component`.
Principais tecnologias envolvidas: 
* Springboot(2.4.0)
* lombok
* gradle
* hibernate
* H2
* junit
* mockito

# Como executar
No diretório raiz: `./gradlew clean build bootRun`. 
A aplicação será executada na porta `8081`.

# Endpoints
#### Cadastro de usuários:
```shell
curl --request POST 'http://localhost:8081/api/customers' \
     --header 'Accept: application/vnd.rmacario.v1+json' \
     --header 'Content-Type: application/vnd.rmacario.v1+json' \
     --data-raw '{
        "name": "Joao da Silva",
        "accountNumber": 2,
        "accountBalance": 500
}'
```

#### Pesquisa de todos os usuários cadastrados:
```shell
curl --request GET 'http://localhost:8081/api/customers?page=<PAGINA_A_PESQUISAR>' \
     --header 'Accept: application/vnd.rmacario.v1+json' \
     --header 'Content-Type: application/vnd.rmacario.v1+json'
```

#### Pesquisa de usuários por número da conta:
```shell
curl --request GET 'http://localhost:8081/api/customers/accounts?number=<NUMER_DA_CONTA>' \
     --header 'Accept: application/vnd.rmacario.v1+json' \
     --header 'Content-Type: application/vnd.rmacario.v1+json'
```
     
#### Transferência de valores entre contas:
```shell
curl --request POST 'http://localhost:8081/api/customers/accounts/movements' \
     --header 'Accept: application/vnd.rmacario.v1+json' \
     --header 'Content-Type: application/vnd.rmacario.v1+json' \
     --data-raw '{
        "accountOrigin": <NUMER_DA_CONTA_ORIGEM>,
        "accountTarget": <NUMER_DA_CONTA_DESTINO>,
        "amount": <VALOR_TRANSFERIDO|ex:10.5>
     }'
```
     
#### Pesquisa de transferências por número da conta:
```shell
curl --request GET 'http://localhost:8081/api/customers/accounts/movements?accountNumber=<NUMER_DA_CONTA>' \
     --header 'Accept: application/vnd.rmacario.v1+json' \
     --header 'Content-Type: application/vnd.rmacario.v1+json'
```
     
*Obs.: O tamanho das páginas nas pesquisas paginadas está fixo em 15 registros.*