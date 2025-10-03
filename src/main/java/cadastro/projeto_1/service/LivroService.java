package cadastro.projeto_1.service;

import cadastro.projeto_1.dao.EmprestimoDAO;
import cadastro.projeto_1.dao.LivroDAO;
import cadastro.projeto_1.model.Emprestimo;
import cadastro.projeto_1.model.Livro;
import cadastro.projeto_1.model.Usuario;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class LivroService {
    private final LivroDAO livroDAO;
    private final EmprestimoDAO emprestimoDAO;

    // Construtor 1: Para Injeção de Dependência (usado principalmente para testes)
    public LivroService(LivroDAO livroDAO, EmprestimoDAO emprestimoDAO) {
        this.livroDAO = livroDAO;
        this.emprestimoDAO = emprestimoDAO;
    }

    // Construtor 2: Sem argumentos (mantido para compatibilidade com a aplicação Swing)
    // Este construtor chama o Construtor 1 com instâncias reais.
    public LivroService() {
        this(new LivroDAO(), new EmprestimoDAO());
    }

    public void adicionarLivro(String titulo, String autor, String isbn) throws SQLException, IllegalArgumentException {
        if (titulo.trim().isEmpty() || autor.trim().isEmpty() || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos são obrigatórios.");
        }
        
        if (livroDAO.isbnExiste(isbn)) {
            throw new IllegalArgumentException("Já existe um livro com este ISBN.");
        }
        
        Livro novoLivro = new Livro(0, titulo, autor, isbn, true, false);
        livroDAO.cadastrar(novoLivro);
    }
    
    public void atualizarLivro(Livro livro, boolean reservadoOriginal) throws SQLException {
        // Lógica: se estava reservado e agora não está, remove a reserva no EmprestimoDAO
        if (reservadoOriginal && !livro.isReservado()) {
            emprestimoDAO.removerReservaPorLivro(livro.getId());
        }
        
        livroDAO.atualizar(livro);
    }
    
    public void excluirLivro(int livroId) throws SQLException {
        // Regra de negócio: remover reservas/empréstimos pendentes antes de excluir o livro.
        emprestimoDAO.removerReservaPorLivro(livroId); 
        livroDAO.excluir(livroId);
    }
    
    public void reservarLivro(int livroId, Usuario usuarioLogado) throws SQLException, IllegalStateException {
        Livro livro = livroDAO.buscarPorId(livroId);
        
        if (livro == null) {
            throw new IllegalStateException("Livro não encontrado.");
        }
        
        if (!livro.isDisponivel() || livro.isReservado()) {
            throw new IllegalStateException("Este livro não está disponível para reserva.");
        }
        
        // Operações de persistência coordenadas
        livroDAO.marcarComoIndisponivel(livroId);
        livroDAO.marcarComoReservado(livroId, true);
        
        Emprestimo reserva = new Emprestimo(
            0, 
            livroId,
            usuarioLogado.getId(),
            null, // dataEmprestimo será NOW() no DAO
            null, 
            null, 
            "reservado", 
            new BigDecimal("0.00") 
        );
        emprestimoDAO.registrarEmprestimo(reserva);
    }
    
    public List<Livro> listarTodos() throws SQLException {
        return livroDAO.listarTodos();
    }
    
    public List<Livro> buscarPorTitulo(String titulo) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            return livroDAO.listarTodos();
        }
        return livroDAO.buscarPorTitulo(titulo);
    }
    
    public Livro buscarPorId(int id) throws SQLException {
        return livroDAO.buscarPorId(id);
    }
}