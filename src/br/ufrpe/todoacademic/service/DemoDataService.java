package br.ufrpe.todoacademic.service;

import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.model.VinculoTarefa;
import br.ufrpe.todoacademic.repository.TarefaRepository;
import java.time.LocalDate;

public class DemoDataService {

    private final AuthService authService;
    private final TarefaService tarefaService;
    private final TarefaRepository repository;

    public DemoDataService(TarefaRepository repository) {
        this.repository = repository;
        this.authService = new AuthService();
        this.tarefaService = new TarefaService(repository);
    }

    public void limparDados() throws Exception {
        repository.limparTudo();
        authService.restaurarAdminPadrao();
    }

    public void gerarDadosDeTeste() throws Exception {
        limparDados();

        // --- 0. PREPARAÇÃO DOS ATORES ---
        Usuario admin = authService.autenticar("admin", "admin"); // Já existe por padrão

        // 2 Professores
        Usuario pGustavo = new Usuario("Prof. Gustavo", "gustavo", "123", TipoUsuario.PROFESSOR);
        Usuario pAna = new Usuario("Profa. Ana", "ana", "123", TipoUsuario.PROFESSOR);
        
        // 4 Alunos
        Usuario sLucas = new Usuario("Lucas", "lucas", "123", TipoUsuario.ALUNO);
        Usuario sGuilherme = new Usuario("Guilherme", "guilherme", "123", TipoUsuario.ALUNO);
        Usuario sSofia = new Usuario("Sofia", "sofia", "123", TipoUsuario.ALUNO);
        Usuario sJulia = new Usuario("Julia", "julia", "123", TipoUsuario.ALUNO);

        authService.cadastrarUsuario(pGustavo);
        authService.cadastrarUsuario(pAna);
        authService.cadastrarUsuario(sLucas);
        authService.cadastrarUsuario(sGuilherme);
        authService.cadastrarUsuario(sSofia);
        authService.cadastrarUsuario(sJulia);

        // ====================================================================
        // CASO 1: ADMIN CRIA -> VÍNCULO NO PROFESSOR (2 Tarefas)
        // Regra: Responsável Criador = Super Admin
        // ====================================================================
        
        // Tarefa 1 (Admin manda Gustavo cadastrar turmas)
        criarTarefa(admin, "Simples", "Cadastrar Turmas 2025.1", "Liberar matrículas no sistema.", 
                "Secretaria", "Urgente", 0, 
                pGustavo); // Vínculo

        // Tarefa 2 (Admin manda Ana entregar diários)
        criarTarefa(admin, "Simples", "Entregar Diários", "Assinar as atas finais.", 
                "Coordenação", "Prazo final", 2, 
                pAna); // Vínculo


        // ====================================================================
        // CASO 2: PROFESSOR CRIA -> VÍNCULO NO ALUNO (4 Tarefas)
        // Regra: Responsável Criador = Nome do Professor
        // ====================================================================

        // Tarefa 1 (Gustavo -> Lucas e Guilherme [Grupo])
        criarTarefa(pGustavo, "Trabalho em Grupo", "Projeto Final Java", "Sistema com persistência.", 
                "Programação II", "Grupo A", 15, 
                sLucas, sGuilherme); // Vínculo Múltiplo

        // Tarefa 2 (Gustavo -> Sofia [Recuperação])
        criarTarefa(pGustavo, "Prova", "Prova de Recuperação", "Assunto: Herança.", 
                "Programação II", "Estudar muito", 5, 
                sSofia); // Vínculo Único

        // Tarefa 3 (Ana -> Julia [Seminário])
        criarTarefa(pAna, "Apresentação", "Seminário de Ética", "Slides sobre LGPD.", 
                "Ética", "10 min", 3, 
                sJulia); // Vínculo Único

        // Tarefa 4 (Ana -> Guilherme [Lista])
        criarTarefa(pAna, "Estudo", "Lista de Exercícios 1", "Resolver questões 1 a 10.", 
                "Lógica", "Valendo ponto", 1, 
                sGuilherme); // Vínculo Único


        // ====================================================================
        // CASO 3: ALUNO CRIA -> SEM VÍNCULO VISUAL (4 Tarefas)
        // Regra: Responsável Criador = Nome do Aluno
        // Regra: "Proibido vínculo na visão do aluno" (Ele cria pra ele mesmo)
        // ====================================================================

        // Tarefa 1 (Lucas)
        criarTarefa(sLucas, "Estudo", "Ler Livro Clean Code", "Capítulo 1 e 2.", 
                "Autoestudo", "Foco", 7); // Sem vínculo explícito

        // Tarefa 2 (Guilherme - Em andamento)
        var tGui = criarTarefa(sGuilherme, "Simples", "Pagar Mensalidade", "Boleto vence hoje.", 
                "Financeiro", "Prioridade Alta", 0);
        tarefaService.iniciarTarefa(tGui.getId());

        // Tarefa 3 (Sofia - Concluída)
        var tSofia = criarTarefa(sSofia, "Simples", "Renovar Livros", "Ir na biblioteca.", 
                "Logística", "Feito", -1);
        tarefaService.concluirTarefa(tSofia.getId());

        // Tarefa 4 (Julia)
        criarTarefa(sJulia, "Trabalho em Grupo", "Reunião do TCC", "Alinhar tema.", 
                "TCC", "Sala 10", 2);
    }

    /**
     * Método auxiliar para criar a tarefa conforme as regras.
     * @param criador Quem está logado criando (Admin, Prof ou Aluno).
     * @param vinculados Lista de usuários que receberão o vínculo (Varargs).
     */
    private Tarefa criarTarefa(Usuario criador, String tipo, String titulo, 
                               String desc, String disc, String notas, int dias, 
                               Usuario... vinculados) throws Exception {
        
        // O campo "Responsável" (String) recebe o nome do CRIADOR
        String nomeResponsavelCriador = criador.getNome();

        // 1. Cria a tarefa
        Tarefa t = tarefaService.cadastrarTarefa(criador, tipo, titulo, desc, disc, nomeResponsavelCriador, notas, LocalDate.now().plusDays(dias));
        
        // 2. Adiciona os Vínculos (apenas para Casos 1 e 2)
        // No Caso 3 (Aluno criando), a lista 'vinculados' vem vazia, então não entra aqui.
        if (vinculados != null) {
            for (Usuario u : vinculados) {
                t.adicionarVinculo(new VinculoTarefa(u));
            }
            // Se adicionou vínculos, atualiza para salvar no arquivo
            if (vinculados.length > 0) {
                repository.atualizar(t);
            }
        }
        
        return t;
    }
}