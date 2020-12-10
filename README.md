
## QuickFIX/J Example

Qiita Java Advent Calendar 2020 11日目の記事のリポジトリです。
JavaのFIXプロトコル実装のためのライブラリQuickFIX/JをSpringBootに載せたサンプルプロジェクトを実装してあります。

このプロジェクトで実装されているのは
- MarketOrder(通称: 成行注文)
- ExecutionResult(通称: 約定情報)

の2つだけです。

### Prerequisites
- Java 11+

### Getting Started

#### Run anyway?

起動するだけであればGradleで起動できます。

```sh
./gradlew bootrun
```

#### Running on docker

Dockerで起動するにはrootディレクトリに配置してあるDockerfileを使ってビルドしてください。

```sh
docker build -t quickfix-example .
docker run -p 8080:8080 --rm quickfix-example
```

## Usage

- PUT `/api/connect`
  - クライアントからカウンターパーティへ接続に行く
- POST `/api/order`
  - クライアントからカウンターパーティへ成行注文を出す

### Example

```
$ curl localhost:8080/api/connect -X PUT
$ curl localhost:8080/api/order -X POST -H 'Content-Type: application/json' -d '{"symbol": "UDSJPY", "qty": 100.00, "side": "1"}'
```
