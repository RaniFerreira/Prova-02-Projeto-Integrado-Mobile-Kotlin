<div align="center">

# 📅 RastreadorPrazo

**Nunca mais perca um vencimento.**
Organize contas, faturas e prazos com lembretes inteligentes, calendário e controle de status — tudo em um app Android moderno.

<br>

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)
![Android](https://img.shields.io/badge/Android%207.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)

![Room](https://img.shields.io/badge/Room-Persistência%20local-0F9D58?style=flat-square)
![WorkManager](https://img.shields.io/badge/WorkManager-Lembretes-FF6F00?style=flat-square)
![Navigation](https://img.shields.io/badge/Navigation-Compose-1E88E5?style=flat-square)

</div>

---

## 📱 Screenshots

<div align="center">

| Lista | Detalhes | Calendário | Ajustes | Notificações |
|:---:|:---:|:---:|:---:|:---:|
| <img width="200" alt="Tela de lista" src="https://github.com/user-attachments/assets/13ff16cd-7e2b-4176-93de-03323248fce4" /> | <img width="200" alt="Tela de detalhes" src="https://github.com/user-attachments/assets/4a455742-18e1-45e2-820e-78d7873e76fa" /> | <img width="200" alt="Tela de calendário" src="https://github.com/user-attachments/assets/538684d7-f9d6-4f91-b9c3-b145d237cdcb" /> | <img width="200" alt="Tela de ajustes" src="https://github.com/user-attachments/assets/29cb2294-1f9e-4e5c-9f73-f475efed1da4" /> | <img width="200" alt="Tela de notificações" src="https://github.com/user-attachments/assets/cbadf5df-f693-4a7b-857a-d9dce8a81cb0" /> |

</div>

---

## ✨ Funcionalidades

| | Recurso | Descrição |
|:---:|---|---|
| 📋 | **Lista de obrigações** | Busca por título ou favorecido e filtro por status |
| ✏️ | **Cadastro e edição** | Título, tipo (Prazo ou Fatura), valor, favorecido, vencimento e horário do lembrete |
| 🔍 | **Detalhes** | Ações rápidas para marcar como paga, cancelar ou excluir |
| 🗓️ | **Calendário** | Visão mensal de todos os vencimentos |
| 🔔 | **Notificações** | Lembretes agendados com WorkManager, com ação *Marcar como paga* direto na notificação |
| ⚙️ | **Ajustes** | Tema sistema/claro/escuro e liga/desliga global de notificações |

### 🏷️ Status das obrigações

> 🟡 **Pendente** — aguardando pagamento
> 🟢 **Paga** — quitada
> 🔴 **Cancelada** — não precisa mais ser paga

---

## 🏗️ Arquitetura

```mermaid
flowchart LR
    UI["🎨 UI<br/>Compose + Material 3"] --> NAV["🧭 Navigation<br/>Compose"]
    UI --> REPO["📦 Repository"]
    REPO --> DAO["🗃️ DAO"]
    DAO --> DB[("💾 Room<br/>Database")]
    REPO --> SET["⚙️ SettingsRepository"]
    REPO --> WM["⏰ WorkManager"]
    WM --> NOTIF["🔔 Notificações"]
```

<details>
<summary><b>📂 Estrutura de pastas</b> (clique para expandir)</summary>

```
app/src/main/java/com/example/rastreadorprazo/
├── data/            # Entidade Obrigacao, DAO, Database, Repository, SettingsRepository
├── notification/    # Agendamento e disparo de notificações (WorkManager)
├── ui/
│   ├── components/  # Componentes reutilizáveis (cards, chips, bottom bar...)
│   ├── navigation/  # Rotas e NavHost
│   ├── screens/     # Lista, Nova/Editar, Detalhes, Calendário, Notificações, Ajustes
│   └── theme/       # Cores, tipografia e tema Material 3
└── MainActivity.kt
```

</details>

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| Interface | Jetpack Compose + Material 3 |
| Persistência | Room |
| Navegação | Navigation Compose |
| Tarefas em segundo plano | WorkManager |

---

## 🚀 Como executar

**Requisitos**

- Android Studio (versão compatível com o AGP/Kotlin do projeto)
- SDK mínimo **24** (Android 7.0) · SDK alvo **36**

**Pelo Android Studio**

1. Abra a pasta `RastreadorPrazo`
2. Aguarde a sincronização do Gradle
3. Rode a configuração `app` em um emulador ou dispositivo físico ▶️

**Pela linha de comando**

```bash
cd RastreadorPrazo
./gradlew installDebug
```

---

<div align="center">

## 👩‍💻 Autora

**Ranielly Ferreira**

Feito com 💜 e Kotlin

</div>
