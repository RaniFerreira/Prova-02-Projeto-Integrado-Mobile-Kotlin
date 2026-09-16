# RastreadorPrazo

Aplicativo Android para organizar contas e prazos a pagar (faturas e prazos em geral), com lembretes por notificação, visualização em calendário e controle de status (pendente, paga, cancelada).

## Funcionalidades

- **Lista de obrigações** com busca por título/favorecido e filtro por status (Todas, Pendentes, Pagas, Canceladas).
- **Cadastro e edição** de obrigações: título, tipo (Prazo ou Fatura), valor, favorecido, data de vencimento e horário do lembrete.
- **Detalhes da obrigação**, com ações rápidas para marcar como paga, cancelar ou excluir.
- **Calendário** com visão mensal dos vencimentos.
- **Notificações** de lembrete agendadas via WorkManager, com opção de marcar como paga direto pela notificação.
- **Ajustes**: tema (sistema/claro/escuro) e liga/desliga global de notificações.

## Tecnologias

- Kotlin
- Jetpack Compose + Material 3
- Room (persistência local)
- Navigation Compose
- WorkManager (agendamento de lembretes)

## Estrutura do projeto

```
app/src/main/java/com/example/rastreadorprazo/
├── data/           # Entidade Obrigacao, DAO, Database, Repository, SettingsRepository
├── notification/   # Agendamento e disparo de notificações (WorkManager)
├── ui/
│   ├── components/ # Componentes reutilizáveis (cards, chips, bottom bar, etc.)
│   ├── navigation/ # Rotas e NavHost
│   ├── screens/    # Telas: Lista, Nova/Editar, Detalhes, Calendário, Notificações, Ajustes
│   └── theme/      # Cores, tipografia e tema Material 3
└── MainActivity.kt
```

## Requisitos

- Android Studio (versão compatível com AGP/Kotlin do projeto)
- SDK mínimo: 24 (Android 7.0)
- SDK alvo: 36

## Como executar

1. Abra a pasta `RastreadorPrazo` no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Rode a configuração `app` em um emulador ou dispositivo físico.

Ou via linha de comando, a partir da pasta `RastreadorPrazo`:

```bash
./gradlew installDebug
```

## Autora

Ranielly Ferreira
