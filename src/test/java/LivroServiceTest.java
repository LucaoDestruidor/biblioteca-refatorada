/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Lucas
 */

import cadastro.projeto_1.dao.EmprestimoDAO;
import cadastro.projeto_1.dao.LivroDAO;
import cadastro.projeto_1.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LivroServiceTest {

    // 1. Mocks: Simulam as dependências (LivroDAO e EmprestimoDAO)
    @Mock
    private LivroDAO livroDAO;

    @Mock
    private EmprestimoDAO emprestimoDAO;

    
    @InjectMocks
    private LivroService livroService;

    @BeforeEach
    public void setUp() {
        
        MockitoAnnotations.openMocks(this);
    }

    // --- Teste 1: Cadastro com sucesso ---
    @Test
    void testAdicionarLivro_Sucesso() throws SQLException {
        // Cenário: ISBN não existe
        when(livroDAO.isbnExiste("978-857522540 8")).thenReturn(false);

        
        livroService.adicionarLivro("Clean Code", "Robert C. Martin", "978-857522540 8");

        // Verificação: O DAO.cadastrar() DEVE ter sido chamado
        verify(livroDAO, times(1)).cadastrar(any());
        // Verificação: O DAO.isbnExiste() DEVE ter sido chamado
        verify(livroDAO, times(1)).isbnExiste("978-857522540 8");
    }

    // --- Teste 2: Falha por campo vazio ---
    @Test
    void testAdicionarLivro_FalhaCampoVazio() throws SQLException {
        
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionarLivro("", "Autor Teste", "12345")
        );
        
        // Verifica a mensagem de erro
        assertEquals("Todos os campos são obrigatórios.", thrown.getMessage());

        // Verifica que o DAO NUNCA foi chamado (a validação impediu a persistência)
        verify(livroDAO, never()).cadastrar(any());
    }

    // --- Teste 3: Falha por ISBN duplicado ---
    @Test
    void testAdicionarLivro_FalhaIsbnDuplicado() throws SQLException {
        String isbnDuplicado = "999-1111111111";

        // Cenário: ISBN JÁ existe
        when(livroDAO.isbnExiste(isbnDuplicado)).thenReturn(true);

        
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> livroService.adicionarLivro("Livro A", "Autor", isbnDuplicado)
        );

        
        assertEquals("Já existe um livro com este ISBN.", thrown.getMessage());

        
        verify(livroDAO, never()).cadastrar(any());
    }
}