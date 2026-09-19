# AppTreino

Aplicativo Android nativo, desenvolvido em **Kotlin** com **Jetpack Compose**, para registro e acompanhamento de treinos de academia e corridas. Permite organizar treinos por exercícios, registrar o histórico de cargas levantadas em cada exercício e acompanhar sessões de corrida (distância, tempo e pace).

## Funcionalidades

- **Treinos**: criação e listagem de treinos, cada um com seus exercícios.
- **Exercícios**: cada treino agrupa exercícios organizados por ordem e grupamento muscular.
- **Histórico de carga**: para cada exercício é possível registrar histórico de cargas (peso, séries e repetições) ao longo do tempo.
- **Corridas**: registro de sessões de corrida com distância, tempo total e pace (min/km).
- **Busca/filtro**: tela dedicada para buscar e filtrar exercícios cadastrados.
- **Navegação por abas**: barra de navegação inferior com três seções principais — Treinos, Corridas e Buscar.

## Arquitetura

O projeto segue uma arquitetura em camadas, próxima de **MVVM**:

- **`data/entity`**: entidades do Room (`TreinoEntity`, `ExercicioEntity`, `CargaHistoricoEntity`, `CorridaEntity`), incluindo chaves estrangeiras com exclusão em cascata (ex.: apagar um treino remove seus exercícios; apagar um exercício remove seu histórico de carga).
- **`data/dao`**: interfaces DAO do Room para acesso ao banco de dados local.
- **`data/relation`**: classes de relacionamento (ex.: `TreinoComExercicios`, `ExercicioComHistorico`) para consultas que combinam múltiplas tabelas.
- **`data/repository`**: repositórios que expõem os dados das DAOs para a camada de UI.
- **`di`**: injeção de dependências simples via `AppContainer` e uma `GenericViewModelFactory` para criação dos ViewModels.
- **`ui/screens`**: telas em Compose organizadas por funcionalidade (`home`, `detalhe`, `historico`, `corrida`, `busca`), cada uma com sua respectiva `Screen` e `ViewModel`.
- **`ui/components`**: componentes de UI reutilizáveis (cards, chips, FAB, cabeçalhos, etc.).
- **`ui/navigation`**: `AppNavHost`, responsável pelas rotas do app e pela barra de navegação inferior.
- **`ui/theme`**: definição de cores, tipografia e dimensões do tema do app.

O banco de dados local é gerenciado com **Room** (`AppDatabase`), usando um singleton para garantir uma única instância durante a execução do app.

## Tecnologias e bibliotecas

- Kotlin
- Jetpack Compose (Material 3)
- Navigation Compose
- Room (persistência local com SQLite)
- Kotlin Coroutines
- Lifecycle ViewModel Compose
- KSP (para geração de código do Room)

## Requisitos

- Android Studio (versão compatível com AGP e Compose atuais)
- JDK 11
- Android SDK: `minSdk` 24, `targetSdk`/`compileSdk` 37

## Como executar

1. Clone este repositório.
2. Abra a pasta `App02Vitor` no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute o app (`Run`) em um emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

Alternativamente, pela linha de comando (a partir da pasta `App02Vitor`):

```bash
./gradlew assembleDebug
```

## Estrutura de pastas

```
App02Vitor/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/apptreino/
│       │   │   ├── data/          # entidades, DAOs, relações e repositórios (Room)
│       │   │   ├── di/            # injeção de dependências
│       │   │   └── ui/            # telas, componentes, navegação e tema (Compose)
│       │   └── res/               # recursos (ícones, strings, cores, temas)
│       ├── test/                  # testes unitários
│       └── androidTest/           # testes instrumentados
├── build.gradle.kts
└── settings.gradle.kts
```

## Contexto

Projeto desenvolvido para a disciplina de Programação para Dispositivos Móveis em Kotlin (Prova 02 — Projeto Integrado Mobile).
