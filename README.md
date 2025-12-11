# TodoAcademic 🎓✅

Aplicativo desktop em **Java Swing** para gerenciamento de tarefas acadêmicas acadêmicas, com suporte a múltiplos usuários e tarefas em grupo.

Projeto da disciplina **Programação II** – **Licenciatura em Computação (UFRPE)**.  
Versão **2.0 (3VA)**, evoluída a partir da versão entregue na **2VA**.

Atende aos requisitos de:

- Herança  
- Polimorfismo  
- Coleções (`ArrayList`, `List` etc.)  
- Classe abstrata  
- Interface  
- Arquitetura em camadas  
- Interface gráfica (GUI)  
- Tratamento de exceções  

---

## 🧠 Ideia do projeto

O **TodoAcademic** é um gerenciador de tarefas voltado para a rotina acadêmica de alunos, professores e administradores.

Cada tarefa pode representar, por exemplo:

- Trabalho em grupo  
- Estudo individual  
- Prova / Apresentação  
- Atividade simples (lista, leitura, exercício etc.)

Principais informações de uma tarefa:

- **Título**
- **Disciplina**
- **Tipo**: Simples, Estudo, Trabalho em Grupo, Prova, Apresentação
- **Data limite** (`DD/MM/AAAA`)
- **Responsável criador** (autor da tarefa)
- **Vínculos com alunos** (para trabalhos em grupo)
- **Notas / observações** (opcional)
- **Descrição detalhada** (opcional)
- **Prioridade** calculada automaticamente (considerando tipo e prazo)

Na interface é possível:

- Fazer **login** com diferentes papéis (Admin, Professor, Aluno)  
- **Criar / visualizar / editar / concluir / excluir** tarefas  
- Vincular **múltiplos alunos** a uma mesma tarefa  
- Visualizar prioridades e status com destaques visuais

---

## 🏛 Arquitetura em camadas

Estrutura simplificada dos pacotes:

```text
src/
 ├─ br.ufrpe.todoacademic.app
 │   └─ TodoAcademicApp.java          # Ponto de entrada (main)
 │
 ├─ br.ufrpe.todoacademic.exception   # Exceções de negócio/persistência
 │   ├─ RepositoryException.java
 │   └─ TarefaInvalidaException.java
 │
 ├─ br.ufrpe.todoacademic.model       # Entidades, enums e hierarquia de tarefas
 │   ├─ StatusTarefa.java
 │   ├─ Tarefa.java                   # Classe abstrata base
 │   ├─ TarefaApresentacao.java
 │   ├─ TarefaEstudo.java
 │   ├─ TarefaProva.java
 │   ├─ TarefaSimples.java
 │   ├─ TarefaTrabalhoGrupo.java
 │   ├─ TipoUsuario.java              # Enum de papéis (ADMIN, PROFESSOR, ALUNO)
 │   ├─ Usuario.java
 │   └─ VinculoTarefa.java            # Relação tarefa x aluno
 │
 ├─ br.ufrpe.todoacademic.repository  # Persistência de dados
 │   ├─ TarefaRepository.java         # Interface de repositório (CRUD)
 │   ├─ TarefaRepositoryArquivo.java  # Implementação com arquivos binários
 │   └─ TarefaRepositoryMemoria.java  # Implementação em memória (apoio/testes)
 │
 ├─ br.ufrpe.todoacademic.resources   # Ícones usados na interface
 │   └─ *.png
 │
 ├─ br.ufrpe.todoacademic.service     # Regras de negócio e serviços
 │   ├─ AuthService.java              # Autenticação e controle de acesso
 │   ├─ DemoDataService.java          # Geração de dados para modo demo
 │   └─ TarefaService.java            # Lógica principal de tarefas
 │
 ├─ br.ufrpe.todoacademic.util
 │   └─ TarefaTableModel.java         # TableModel da JTable de tarefas
 │
 └─ br.ufrpe.todoacademic.view        # Interface gráfica (Swing)
     ├─ CalendarDialog.java           # Seletor de data com calendário
     ├─ HeaderPanel.java              # Cabeçalho com logo/usuário
     ├─ LoginScreen.java              # Tela de login
     ├─ MainScreen.java               # Tela principal (JFrame)
     ├─ PrioridadeSidebar.java        # Painel lateral com regras de prioridade
     ├─ TarefaFormDialog.java         # Formulário de cadastro/edição (JDialog)
     ├─ TarefaRenderers.java          # Renderização customizada da JTable
     ├─ UsuarioFormDialog.java        # Cadastro/edição de usuários
     ├─ UsuarioListScreen.java        # Lista de usuários (Admin)
     └─ VinculosDialog.java           # Gestão de vínculos aluno x tarefa
```

---

## 💾 Persistência e Controle de Acesso (novidades da v2.0)

### Persistência em arquivos

A versão 2.0 substitui o armazenamento apenas em memória por **persistência em arquivos binários**, usando **serialização Java**:

- Arquivos gerados:
  - `tarefas_db.dat` → lista de tarefas e vínculos.
  - `usuarios_db.dat` → usuários cadastrados e credenciais.
- Classes `Tarefa`, `Usuario` e `VinculoTarefa` implementam `Serializable`.
- A classe `TarefaRepositoryArquivo` usa `ObjectOutputStream` e `ObjectInputStream` para gravar e ler coleções completas.

Isso permite fechar o sistema e voltar depois sem perder os dados.

### Controle de acesso (RBAC)

O sistema possui três tipos de usuário (`TipoUsuario`):

- **ADMIN**
  - Acesso total.
  - Gerencia usuários.
  - Pode criar tarefas em nome de qualquer usuário.
  - Acessa o **Modo Demo** (limpar e gerar dados).

- **PROFESSOR**
  - Visualiza todas as tarefas.
  - Cria tarefas onde ele é o criador (campo travado).
  - Pode editar/excluir tarefas de alunos.

- **ALUNO**
  - Vê apenas tarefas criadas por ele ou em que está vinculado.
  - Cria tarefas apenas para si mesmo.
  - Não pode editar/excluir tarefas de professores/admin.

Além disso, há a separação entre:

- **Responsável Criador**: usuário que criou a tarefa (autor).  
- **Vínculos (VinculoTarefa)**: lista de alunos participantes daquela tarefa, cada um com status, prioridade manual e observações individuais.

---

## 🎨 Interface gráfica e usabilidade

A interface é construída em **Java Swing** com tema moderno via **FlatLaf**.

### Tela de Login

- Autenticação via `AuthService`.
- Validação básica de usuário/senha.
- Redirecionamento para a `MainScreen` de acordo com o papel.

### Tela Principal (MainScreen)

- Tabela de tarefas com renderização personalizada (`TarefaRenderers`):
  - **Badges de status** coloridos (`Graphics2D` + `fillRoundRect`).
  - Coluna de responsável com prefixos como `Prof.:` e `Aluno:`.
  - Tooltips na coluna de prioridade explicando o significado do número.
- Botões de ação (Nova, Editar, Concluir, Excluir, Usuários, Demo) que:
  - Habilitam/desabilitam conforme seleção da tabela.
  - Respeitam as permissões do usuário logado.
  - Explicam via tooltip quando a ação não é permitida.

### Formulário de Tarefa (TarefaFormDialog)

- Campo de data com `MaskFormatter` (`DD/MM/AAAA`) + botão que abre o `CalendarDialog`.
- Sidebar (`PrioridadeSidebar`) com texto em HTML explicando as regras de prioridade conforme o tipo da tarefa.
- Botão **“Alunos vinculados”** que abre o `VinculosDialog` para adicionar/remover alunos da tarefa.

---

## 🧩 Requisitos da disciplina (a–h)

**a. Herança**  
- `Tarefa` é a classe abstrata base.  
- `TarefaSimples`, `TarefaEstudo`, `TarefaProva`, `TarefaTrabalhoGrupo` e `TarefaApresentacao` estendem `Tarefa`.

**b. Polimorfismo**  
- O sistema trabalha com coleções de `Tarefa` (tipo genérico).  
- Métodos como `calcularPrioridade()` e `getTipo()` são sobrescritos nas subclasses e usados de forma polimórfica na `TarefaService` e na tabela.

**c. Coleções (`ArrayList`, `List`, ...)**  
- Repositórios utilizam coleções (`List<Tarefa>`, `List<Usuario>`, `List<VinculoTarefa>`).  
- Essas coleções são serializadas para disco e exibidas na JTable via `TarefaTableModel`.

**d. Classe abstrata**  
- `Tarefa` é `abstract`, não pode ser instanciada diretamente e define a estrutura mínima de uma tarefa.

**e. Interface**  
- `TarefaRepository` é uma interface que define as operações de CRUD.  
- `TarefaRepositoryArquivo` e `TarefaRepositoryMemoria` são implementações concretas que podem ser trocadas sem alterar a camada de serviço.

**f. Arquitetura em camadas**  
- Separação entre:
  - `model` (domínio),
  - `repository` (persistência),
  - `service` (regras de negócio),
  - `view` (interface gráfica),
  - `app` (inicialização),
  - `exception` (erros específicos).
- A view conversa apenas com os services, que por sua vez usam os repositórios.

**g. Interface gráfica (GUI)**  
- Construída com **Swing**: `JFrame`, `JDialog`, `JTable`, `JButton`, `JLabel`, `JTextField`, `JTextArea`, `JComboBox`, etc.  
- Uso de **FlatLaf** para tema moderno, ícones e renderização customizada para melhorar a experiência do usuário.

**h. Tratamento de exceções**  
- Uso de `try/catch` em:
  - Conversão de datas,
  - Operações de leitura/escrita de arquivos,
  - Configuração de LookAndFeel,
  - Autenticação e regras de negócio.  
- Exceções específicas:
  - `TarefaInvalidaException` para erros de domínio,
  - `RepositoryException` para falhas na camada de dados.  
- Mensagens de erro amigáveis exibidas via `JOptionPane`.

---

## 🖥️ Fluxo básico de uso

1. **Executar a aplicação**  
   - A classe principal é `br.ufrpe.todoacademic.app.TodoAcademicApp`.

2. **Login**  
   - Informar usuário e senha válidos (há um administrador padrão configurado no código para fins de teste/demonstração).

3. **Uso como Admin**
   - Gerenciar usuários (professores e alunos).  
   - Cadastrar tarefas para qualquer usuário.  
   - Acessar o **Modo Demo** para limpar e gerar dados de exemplo.

4. **Uso como Professor / Aluno**
   - Visualizar tarefas de acordo com as permissões do papel.  
   - Criar, editar e concluir tarefas respeitando as regras de RBAC.  
   - Gerenciar vínculos de alunos em tarefas de grupo (quando permitido).

---

## 🔧 Tecnologias

- **Linguagem:** Java  
- **JDK:** 17+ (testado com versões modernas de JDK)  
- **IDE:** NetBeans (projeto Ant)  
- **GUI:** Java Swing + FlatLaf  
- **Persistência:** Serialização em arquivos binários (`.dat`)  
- **Coleções:** `List`, `ArrayList`  
- **Controle de versão:** Git / GitHub  

---

## 🚀 Como executar

1. Abrir o projeto no **NetBeans**:
   - `File > Open Project...`
   - Selecionar a pasta `TodoAcademic` (onde ficam `build.xml` e `nbproject/`).
   - Clicar em **Open Project**.

2. Conferir a biblioteca do FlatLaf:
   - Verificar se o `flatlaf-*.jar` está listado em **Libraries**.
   - Caso não esteja, adicionar o JAR manualmente ao projeto.

3. Rodar a aplicação:
   - Botão direito no projeto → **Run**  
   - Classe principal: `br.ufrpe.todoacademic.app.TodoAcademicApp`  
   - A tela de login será aberta, seguida da `MainScreen` após autenticação.
