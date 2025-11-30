
# TodoAcademic 🎓✅
Aplicativo desktop em **Java Swing** para gerenciamento de tarefas acadêmicas em grupo.

Projeto desenvolvido na disciplina **Programação II** do curso de **Licenciatura em Computação – UFRPE**, atendendo aos requisitos de:
- Herança
- Polimorfismo
- Coleções (`ArrayList`)
- Classe abstrata
- Interface
- Arquitetura em camadas
- Interface gráfica (GUI)
- Tratamento de exceções

---

## 🧠 Ideia do Projeto

O **TodoAcademic** é um gerenciador simples de tarefas para um grupo de alunos. Cada tarefa pode representar:

- Trabalho em grupo  
- Estudo individual  
- Atividade simples (lista, leitura, etc.)

Campos principais da tarefa:

- **Título**
- **Disciplina**
- **Responsável**
- **Tipo**: Simples, Estudo ou Trabalho em Grupo
- **Data limite** (prazo) – formato `dd/MM/yyyy`
- **Notas/observações** (opcional)
- **Descrição** (opcional)

A tabela de tarefas permite:

- Criar
- Editar
- Concluir
- Excluir tarefas

---

## 🏛 Arquitetura em Camadas

Estrutura simplificada dos pacotes:

```text
src/
 ├─ br.ufrpe.todoacademic.app
 │   └─ TodoAcademicApp.java      # Classe com o método main
 │
 ├─ br.ufrpe.todoacademic.model   # Entidades e hierarquia de tarefas
 │   ├─ Tarefa.java               # Classe abstrata base
 │   ├─ TarefaSimples.java
 │   ├─ TarefaEstudo.java
 │   ├─ TarefaTrabalhoGrupo.java
 │   └─ StatusTarefa.java         # Enum com estados da tarefa
 │
 ├─ br.ufrpe.todoacademic.repository
 │   ├─ TarefaRepository.java         # Interface de repositório (CRUD)
 │   └─ TarefaRepositoryMemoria.java  # Implementação usando ArrayList
 │
 ├─ br.ufrpe.todoacademic.service
 │   └─ TarefaService.java        # Regras de negócio e validações
 │
 ├─ br.ufrpe.todoacademic.exception
 │   ├─ TarefaInvalidaException.java
 │   └─ RepositoryException.java
 │
 ├─ br.ufrpe.todoacademic.util
 │   └─ TarefaTableModel.java     # TableModel para a JTable
 │
 └─ br.ufrpe.todoacademic.view    # Interface gráfica (Swing)
     ├─ MainScreen.java           # Tela principal (JFrame)
     └─ TarefaFormDialog.java     # Formulário de cadastro/edição (JDialog)
```

---

## ✅ Como o projeto atende aos requisitos

- **Herança / Classe abstrata**  
  - `Tarefa` é uma classe abstrata base.  
  - `TarefaSimples`, `TarefaEstudo` e `TarefaTrabalhoGrupo` herdam de `Tarefa`.

- **Polimorfismo**  
  - `List<Tarefa>` armazena qualquer subtipo de tarefa.  
  - Métodos como `getTipo()` e `calcularPrioridade()` são sobrescritos nas subclasses.

- **Coleções (`ArrayList`)**  
  - `TarefaRepositoryMemoria` usa `ArrayList<Tarefa>` para armazenar os dados em memória.

- **Interface**  
  - `TarefaRepository` define as operações de repositório.  
  - `TarefaRepositoryMemoria` implementa essa interface.

- **Arquitetura em camadas**  
  - Separação em Model, Repository, Service, View, Exception e App (main).

- **GUI (interface gráfica)**  
  - Construída com Swing: `JFrame`, `JDialog`, `JTable`, `JButton`, `JLabel`, etc.  
  - Layouts: `GroupLayout`, `BorderLayout`, `GridBagLayout`.  
  - Ícones em `src/resources` para melhorar a apresentação.

- **Tratamento de exceções**  
  - `TarefaInvalidaException` para validações de domínio.  
  - `RepositoryException` para erros na camada de repositório.  
  - `DateTimeParseException` tratada na leitura da data.  
  - Mensagens amigáveis via `JOptionPane`.

> 💡 Decisão de projeto: as tarefas são salvas somente em memória (`ArrayList`).  
> Ao fechar o aplicativo, os dados são perdidos. Isso simplifica o foco didático em OO, camadas, GUI, herança e polimorfismo.

---

## 🖥️ Funcionalidades

- Listar tarefas em uma tabela.  
- Cadastrar nova tarefa (com tipo, título, disciplina, responsável, prazo e campos opcionais).  
- Editar tarefa existente.  
- Marcar tarefa como concluída.  
- Excluir tarefa com confirmação.  
- Mensagem amigável quando não há tarefas cadastradas.

---

## 🔧 Tecnologias

- **Linguagem:** Java  
- **Versão:** JDK 8+  
- **IDE:** NetBeans (projeto Ant padrão)  
- **GUI:** Java Swing  
- **Coleções:** `List`, `ArrayList`  
- **Controle de versão:** Git / GitHub  

---

## 🚀 Como executar

### 1. Abrir no NetBeans

1. Abra o **NetBeans**.  
2. Vá em **File > Open Project...**.  
3. Selecione a pasta `TodoAcademic` (onde estão `build.xml` e `nbproject/`).  
4. Clique em **Open Project**.

### 2. Rodar a aplicação

- Clique com o botão direito no projeto → **Run**  
  - A classe principal é `br.ufrpe.todoacademic.app.TodoAcademicApp`.  
- A tela principal (**MainScreen**) será aberta com os botões:
  - **Nova tarefa**, **Editar**, **Concluir**, **Excluir**.
