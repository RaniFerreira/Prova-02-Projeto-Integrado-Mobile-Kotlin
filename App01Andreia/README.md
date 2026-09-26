# App Leituras

Aplicativo Android nativo, desenvolvido em **Kotlin** com **Jetpack Compose**, para organizar e acompanhar leituras. Permite montar um catálogo de livros (cadastrados à mão ou buscados na API do Google Books), registrar sessões de leitura com cronômetro, definir metas e manter um diário de notas para cada livro.

## Screenshots

| Dashboard | Detalhes do livro | Sessão de leitura | Definir meta |
|:---:|:---:|:---:|:---:|
| <img alt="image" src="https://github.com/user-attachments/assets/cd76d069-a258-4aa5-975c-2c52891caee1" width="200" /> |<img alt="image" src="https://github.com/user-attachments/assets/88578290-0389-4a02-a400-c7a9f6183cb9" width="200" /> | <img alt="image" src="https://github.com/user-attachments/assets/a2fa4921-ab64-4809-a00c-bc9a5f0b460a" width="200" /> | <img alt="image" src="https://github.com/user-attachments/assets/91f56172-4284-4bdc-8269-023e8c3e0390" width="200" /> |

| Diário de notas | Buscar livro | Novo livro |
|:---:|:---:|:---:|
| <img alt="image" src="https://github.com/user-attachments/assets/07ad4571-eae3-4352-abe5-d90071ef8f6e" width="200" /> | <img alt="image" src="https://github.com/user-attachments/assets/434ead6b-bb17-4e14-b170-bd4d2ee1046a" width="200" /> | <img alt="image" src="https://github.com/user-attachments/assets/85740026-e490-4ffc-96e4-ae4c51de663f" width="200" /> |

## Funcionalidades

- **Dashboard**: livros separados em "Em andamento", "Quero ler" e "Lido", com filtros por gênero e por status.
- **Busca no Google Books**: pesquisa livros por título ou autor e pré-preenche o cadastro com título, autor, páginas, gênero e capa.
- **Cadastro manual**: formulário para livros que não estão na API, com escolha de capa pela galeria.
- **Sem duplicados**: o app reconhece um livro já cadastrado (pelo ID do Google Books ou por título + autor) e não cria outro registro.
- **Sessão de leitura**: cronômetro para registrar o tempo lido e a página alcançada. A página atual e o progresso do livro são atualizados na hora.
- **Conclusão automática**: ao chegar à última página, o livro passa para "Lido".
- **Metas**: tempo previsto de leitura e data para terminar cada livro.
- **Diário**: notas, insights e citações por livro.

## Arquitetura

O projeto segue **MVVM** com **Repository Pattern**, dividido em camadas:

```
Tela (Compose) → ViewModel → Repository (interface) → RepositoryImpl → Room / Google Books API
      ↑               │
      └── StateFlow ──┘
```

- **`ui/`**: telas em Compose, organizadas por funcionalidade (`dashboard`, `bookdetail`, `sessaoleitura`, `definirmeta`, `diario`, `buscarlivro`, `novolivro`), cada uma com sua `Screen` e seu `ViewModel`. Também tem `components` (peças reaproveitadas), `navigation` (rotas) e `theme`.
- **`domain/model`**: modelos usados pelas telas (`Livro`, `SessaoLeitura`, `Meta`, `Nota`...).
- **`domain/repository`**: interfaces dos repositórios. Os ViewModels só conhecem essas interfaces.
- **`data/repository`**: implementações dos repositórios e regras de negócio (evitar duplicados, avançar página, marcar como lido).
- **`data/local`**: banco Room (`AppDatabase`), entidades (`entity/`) e DAOs (`dao/`).
- **`data/remote`**: acesso à API do Google Books com Retrofit, e DTOs do JSON (`dto/`).
- **`data/mapper`**: conversão entre entidades/DTOs e os modelos do domain.
- **`di/`**: módulos do **Hilt** que criam o banco, o Retrofit e ligam cada interface à sua implementação.

As DAOs retornam `Flow`, e os ViewModels expõem `StateFlow`. Assim, qualquer mudança no banco (como registrar uma sessão) atualiza as telas automaticamente, sem recarregar nada à mão.

Na primeira abertura, o banco é criado com 6 livros de exemplo.

## Tecnologias e bibliotecas

- Kotlin
- Jetpack Compose (Material 3)
- Navigation Compose
- Hilt (injeção de dependência)
- Room (persistência local com SQLite)
- Retrofit + OkHttp (Google Books API)
- Coil (carregamento das capas)
- Kotlin Coroutines e Flow
- KSP (geração de código do Room e do Hilt)

## Requisitos

- Android Studio (versão compatível com AGP 9 e Compose atuais)
- JDK 11
- Android SDK: `minSdk` 24, `targetSdk`/`compileSdk` 37
- Uma chave da **Google Books API** (sem ela, a busca de livros não funciona)

## Como executar

1. Clone este repositório.
2. Abra a pasta `App01Andreia` no Android Studio.
3. No arquivo `local.properties` (na raiz de `App01Andreia`, fora do controle do git), adicione a sua chave:
   ```properties
   GOOGLE_BOOKS_API_KEY=sua_chave_aqui
   ```
4. Aguarde a sincronização do Gradle.
5. Execute o app (`Run`) em um emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

Alternativamente, pela linha de comando (a partir da pasta `App01Andreia`):

```bash
./gradlew assembleDebug
```

## Estrutura de pastas

```
App01Andreia/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/app_leituras/
│       │   │   ├── data/          # Room, Google Books API, mappers e repositórios
│       │   │   ├── di/            # módulos do Hilt
│       │   │   ├── domain/        # modelos e interfaces dos repositórios
│       │   │   └── ui/            # telas, componentes, navegação e tema (Compose)
│       │   └── res/               # recursos (ícones, strings, cores, temas)
│       ├── test/                  # testes unitários
│       └── androidTest/           # testes instrumentados
├── screenshots/                   # capturas do app rodando
├── build.gradle.kts
└── settings.gradle.kts
```

## Contexto

Projeto desenvolvido para a disciplina de Programação para Dispositivos Móveis em Kotlin (Prova 02 — Projeto Integrado Mobile).

## Autora

Andréia Carvalho
