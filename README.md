# TodoAcademic 🎓✅

Aplicativo desktop em **Java Swing** para gerenciamento de tarefas acadêmicas em grupo.

Projeto da disciplina **Programação II** – **Licenciatura em Computação (UFRPE)**.

Atende aos requisitos de:

- Herança  
- Polimorfismo  
- Coleções (`ArrayList`)  
- Classe abstrata  
- Interface  
- Arquitetura em camadas  
- Interface gráfica (GUI)  
- Tratamento de exceções  

---

## 🧠 Ideia do projeto

O **TodoAcademic** é um gerenciador de tarefas voltado para a rotina acadêmica de um grupo de alunos.  
Cada tarefa pode representar, por exemplo:

- Trabalho em grupo  
- Estudo individual  
- Atividade simples (lista, leitura, exercício etc.)

Cada tarefa possui:

- **Título**
- **Disciplina**
- **Responsável**
- **Tipo**: Simples, Estudo, Trabalho em Grupo, Prova, Apresentação
- **Data limite** (`DD/MM/AAAA`)
- **Notas / observações** (opcional)
- **Descrição detalhada** (opcional)

Na tela principal é possível:

- **Criar** tarefas  
- **Visualizar / Editar** tarefas  
- **Concluir** tarefas  
- **Excluir** tarefas  

Quando não há tarefas, a aplicação exibe uma mensagem amigável orientando o usuário a criar a primeira.

---

## 🏛 Arquitetura em camadas

Estrutura simplificada dos pacotes:

```text
src/
 ├─ br.ufrpe.todoacademic.app
 │   └─ TodoAcademicApp.java        # Ponto de entrada (main)
 │
 ├─ br.ufrpe.todoacademic.model     # Entidades e hierarquia de tarefas
 │   ├─ Tarefa.java                 # Classe abstrata base
 │   ├─ TarefaSimples.java
 │   ├─ TarefaEstudo.java
 │   ├─ TarefaTrabalhoGrupo.java
 │   ├─ TarefaProva.java
 │   └─ StatusTarefa.java           # Enum com estados da tarefa
 │
 ├─ br.ufrpe.todoacademic.repository
 │   ├─ TarefaRepository.java           # Interface de repositório (CRUD)
 │   └─ TarefaRepositoryMemoria.java    # Implementação usando ArrayList
 │
 ├─ br.ufrpe.todoacademic.service
 │   └─ TarefaService.java          # Regras de negócio e validações
 │
 ├─ br.ufrpe.todoacademic.exception
 │   ├─ TarefaInvalidaException.java
 │   └─ RepositoryException.java
 │
 ├─ br.ufrpe.todoacademic.util
 │   └─ TarefaTableModel.java       # TableModel da JTable de tarefas
 │
 └─ br.ufrpe.todoacademic.view      # Interface gráfica (Swing)
     ├─ MainScreen.java             # Tela principal (JFrame)
     └─ TarefaFormDialog.java       # Formulário de cadastro/edição (JDialog)
```

---

## ✅ Como o projeto atende aos requisitos

- **Herança / Classe abstrata**  
  - `Tarefa` é uma classe abstrata base.  
  - `TarefaSimples`, `TarefaEstudo`, `TarefaTrabalhoGrupo` e `TarefaProva` herdam de `Tarefa`.

- **Polimorfismo**  
  - A aplicação trabalha com listas de `Tarefa` (tipo genérico).  
  - Métodos como `calcularPrioridade()` e `getTipo()` são sobrescritos nas subclasses e usados de forma polimórfica.

- **Coleções (`ArrayList`)**  
  - `TarefaRepositoryMemoria` utiliza `ArrayList<Tarefa>` para armazenar as tarefas em memória.  

- **Interface**  
  - `TarefaRepository` define o contrato do repositório (métodos de CRUD).  
  - `TarefaRepositoryMemoria` implementa essa interface.

- **Arquitetura em camadas**  
  - Separação clara em:
    - `model` (domínio)
    - `repository` (dados)
    - `service` (regras de negócio)
    - `view` (GUI)
    - `app` (bootstrap / main)
    - `exception` (tratamento de erros específicos)

- **Interface gráfica (GUI)**  
  - Construída com Java Swing:
    - `JFrame`, `JDialog`, `JTable`, `JButton`, `JLabel`, `JTextField`, `JTextArea`, `JComboBox` etc.  
  - Uso de ícones e cores para melhorar a visualização da prioridade das tarefas.

- **Tratamento de exceções**  
  - `TarefaInvalidaException` para erros de validação de domínio.  
  - `RepositoryException` para problemas na camada de repositório.  
  - `DateTimeParseException` tratada ao ler a data digitada pelo usuário.  
  - Exibição de mensagens amigáveis via `JOptionPane`.

> 💡 As tarefas são salvas em memória (via `ArrayList`).  
> Ao fechar o aplicativo, os dados são descartados. A ideia é manter o foco em orientação a objetos, camadas, GUI, herança e polimorfismo.

---

## 🖥️ Funcionalidades da aplicação

- Listar todas as tarefas em uma tabela.  
- Cadastrar novas tarefas com tipo, título, disciplina, responsável e prazo.  
- Editar tarefas existentes.  
- Marcar tarefas como concluídas.  
- Excluir tarefas com confirmação.  
- Ver detalhes de uma tarefa em modo só leitura.  
- Exibir regras de prioridade na lateral do formulário, de acordo com o tipo escolhido.

---

## 🔧 Tecnologias

- **Linguagem:** Java  
- **Versão:** JDK 8+  
- **IDE:** NetBeans (projeto Ant)  
- **GUI:** Java Swing  
- **Coleções:** `List`, `ArrayList`  
- **Controle de versão:** Git / GitHub  

---

## 🚀 Como executar

### 1. Abrir o projeto no NetBeans

1. Abrir o **NetBeans**.  
2. Ir em **File > Open Project...**.  
3. Selecionar a pasta do projeto (`TodoAcademic`), onde ficam `build.xml` e `nbproject/`.  
4. Clicar em **Open Project**.

### 2. Rodar a aplicação

1. Botão direito no projeto → **Run**.  
2. A classe principal é:

   ```text
   br.ufrpe.todoacademic.app.TodoAcademicApp
   ```

3. A tela principal (**MainScreen**) será aberta com os botões de gestão de tarefas.
