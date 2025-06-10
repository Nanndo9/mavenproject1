/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package AlunoApp.src.ui;

import AlunoApp.src.bean.Aluno;
import AlunoApp.src.exception.MatriculaDuplicadaException;
import AlunoApp.src.bean.AlunoDAO;
import AlunoApp.src.bean.RemocaoAlunoDAO;
import AlunoApp.src.bean.AlunoDAOHibernate;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Calendar;

public class AlunoForm extends javax.swing.JFrame {

    // --- Componentes da Interface Gráfica ---  
    private JTextField txtMatricula, txtNome;
    private JTextField txtIdadeCalculadaDisplay;
    private JFormattedTextField txtDataNascimento, txtTelefone, txtCpf;
    private JButton btnAdicionar, btnBuscar, btnRemover, btnVerificarIdades, btnInserirPosicao, btnListarTodos;

    // --- Lista de Alunos e Utilitários ---
    private List<Aluno> listaAlunos = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static final String NOME_ARQUIVO = "ListagemAlunos.txt";
    private AlunoDAOHibernate alunoDAOHibernate = new AlunoDAOHibernate();
    
    // NOVA VARIÁVEL: Para controlar o modo de edição
    private boolean modoEdicao = false;
    private String matriculaEmEdicao = "";

    // VARIÁVEIS DE INSTÂNCIA: Adicione estas no início da classe
    private JButton btnSalvarAlteracoes, btnCancelarEdicao;

    public AlunoForm() {
        // --- Configurações da Janela Principal ---
        setTitle("Cadastro de Alunos");
        setSize(600, 400);
        setMinimumSize(new Dimension(900, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        sdf.setLenient(false);
        carregarDeCSV();

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createTitledBorder("Dados do Aluno"));

        // Voltamos ao GridBagLayout simples, alinhado à esquerda
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; // Garante alinhamento à esquerda

        // --- Linha 0: Matrícula ---
        gbc.gridx = 0; // Coluna 0
        gbc.gridy = 0;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Matrícula:"), gbc);

        gbc.gridx = 1; // Coluna 1
        gbc.weightx = 1.0;
        txtMatricula = new JTextField(30);
        panelForm.add(txtMatricula, gbc);

        // --- Linha 1: Nome ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNome = new JTextField(30);
        panelForm.add(txtNome, gbc);

        // --- Linha 2: Data de Nascimento e Idade ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Data Nascimento:"), gbc);

        // O painel que agrupa data e idade
        JPanel painelDataIdade = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        txtDataNascimento = new JFormattedTextField(criarMascara("##/##/####"));
        txtDataNascimento.setColumns(7);
        txtDataNascimento.addPropertyChangeListener(evt -> atualizarIdadeCalculada());

        txtIdadeCalculadaDisplay = new JTextField(5);
        txtIdadeCalculadaDisplay.setEditable(false);
        txtIdadeCalculadaDisplay.setFont(new Font("Monospaced", Font.BOLD, 12));
        txtIdadeCalculadaDisplay.setBackground(UIManager.getColor("Label.background"));

        painelDataIdade.add(txtDataNascimento);

        // <<< MUDANÇA PRINCIPAL AQUI >>>
        // Adiciona um espaçador invisível para empurrar a idade para a direita
        painelDataIdade.add(Box.createHorizontalStrut(20)); // Altere 20 para o espaço que desejar

        painelDataIdade.add(new JLabel("Idade:"));
        painelDataIdade.add(txtIdadeCalculadaDisplay);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelForm.add(painelDataIdade, gbc);

        // --- Linha 3: Telefone ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panelForm.add(new JLabel("Telefone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtTelefone = new JFormattedTextField(criarMascara("(##) #####-####"));
        txtTelefone.setColumns(15);
        panelForm.add(txtTelefone, gbc);

        // --- Linha 4: CPF ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panelForm.add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCpf = new JFormattedTextField(criarMascara("###.###.###-##"));
        txtCpf.setColumns(15);
        panelForm.add(txtCpf, gbc);

        // Espaçador vertical para empurrar tudo para cima
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        panelForm.add(new JPanel(), gbc);

// Adiciona o painel pronto ao Norte do Frame
        add(panelForm, BorderLayout.NORTH);

        // --- Painel de Botões ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("Adicionar");
        btnBuscar = new JButton("Pesquisar Aluno");
        btnRemover = new JButton("Remover Aluno");
        btnVerificarIdades = new JButton("Mais Novo/Velho");
        btnInserirPosicao = new JButton("Inserir em Posição");
        btnListarTodos = new JButton("Listar Todos");

        // NOVO BOTÃO
        JButton btnAtualizar = new JButton("Atualizar Aluno");

        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnBuscar);
        panelBotoes.add(btnRemover);
        panelBotoes.add(btnAtualizar); // Adicionar o novo botão
        panelBotoes.add(btnVerificarIdades);
        panelBotoes.add(btnInserirPosicao);
        panelBotoes.add(btnListarTodos);
        add(panelBotoes, BorderLayout.SOUTH);

        // --- Adicionando Ações aos Botões ---
        btnAdicionar.addActionListener(this::adicionarAluno);
        btnBuscar.addActionListener(this::buscarAluno);
        btnRemover.addActionListener(this::removerAluno);
        btnAtualizar.addActionListener(this::atualizarAluno); // NOVA AÇÃO
        btnVerificarIdades.addActionListener(this::verificarIdades);
        btnInserirPosicao.addActionListener(this::inserirEmPosicao);
        btnListarTodos.addActionListener(e -> {
            if (listaAlunos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A lista de alunos está vazia.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            TabelaAlunosDialog dialogTabela = new TabelaAlunosDialog(this, listaAlunos);
            dialogTabela.setVisible(true);
        });

        try {
            System.out.println("Tentando inicializar o Hibernate...");
            org.hibernate.Session session = AlunoApp.src.util.HibernateUtil.getSessionFactory().openSession();
            System.out.println("Sessão do Hibernate criada com sucesso!");
            session.close();
        } catch (Exception ex) {
            System.err.println("ERRO AO INICIALIZAR HIBERNATE: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro na conexão com o banco de dados: " + ex.getMessage(), 
                "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private MaskFormatter criarMascara(String formato) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(formato);
            formatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar a máscara de formatação: " + e.getMessage(), "Erro Interno", JOptionPane.ERROR_MESSAGE);
        }
        return formatter;
    }

    private int calcularIdade(Date dataNascimento) {
        if (dataNascimento == null) {
            return 0;
        }
        Calendar dataNasc = Calendar.getInstance();
        dataNasc.setTime(dataNascimento);
        Calendar hoje = Calendar.getInstance();

        int idade = hoje.get(Calendar.YEAR) - dataNasc.get(Calendar.YEAR);
        if (hoje.get(Calendar.MONTH) < dataNasc.get(Calendar.MONTH)
                || (hoje.get(Calendar.MONTH) == dataNasc.get(Calendar.MONTH) && hoje.get(Calendar.DAY_OF_MONTH) < dataNasc.get(Calendar.DAY_OF_MONTH))) {
            idade--;
        }
        return idade < 0 ? 0 : idade;
    }

    // NOVO: Método para atualizar o JLabel da idade calculada
    private void atualizarIdadeCalculada() {
        try {
            String dataNascStr = txtDataNascimento.getText();

            if (dataNascStr != null && !dataNascStr.contains("_") && dataNascStr.length() == 10) {
                Date dataNascimento = sdf.parse(dataNascStr);
                
                Date hoje = new Date();
                if (dataNascimento.after(hoje)) {
                    // Data de nascimento é futura
                    JOptionPane.showMessageDialog(this, 
                        "Erro: Data de nascimento não pode ser posterior à data atual!\n" +
                        "Data informada: " + dataNascStr + "\n" +
                        "Data atual: " + sdf.format(hoje), 
                        "Data Inválida", 
                        JOptionPane.ERROR_MESSAGE);
                    
                    // Limpar o campo de data e idade
                    txtDataNascimento.setValue(null);
                    txtIdadeCalculadaDisplay.setText("");
                    return;
                }
                
                int idade = calcularIdade(dataNascimento);
                txtIdadeCalculadaDisplay.setText(String.valueOf(idade));
            } else {
                // Se a data não estiver completa, limpa o campo de idade
                txtIdadeCalculadaDisplay.setText("");
            }
        } catch (ParseException ex) {
            // Se a data for inválida (ex: 30/02/2020), limpa também
            txtIdadeCalculadaDisplay.setText("");
            
            // NOVA VALIDAÇÃO: Mostrar mensagem para datas inválidas
            JOptionPane.showMessageDialog(this, 
                "Erro: Data de nascimento inválida!\n" +
                "Verifique se a data está no formato correto (dd/MM/yyyy) e se existe.\n" +
                "Exemplo: 29/02 só existe em anos bissextos.", 
                "Data Inválida", 
                JOptionPane.ERROR_MESSAGE);
            
            // Limpar o campo
            txtDataNascimento.setValue(null);
        }
    }

    // MÉTODO CORRIGIDO: Atualizar aluno
    private void atualizarAluno(ActionEvent e) {
        String matricula = JOptionPane.showInputDialog(this, "Digite a matrícula do aluno a ser atualizado:");
        if (matricula == null || matricula.trim().isEmpty()) {
            return;
        }

        try {
            // Buscar o aluno no banco de dados
            Aluno alunoExistente = alunoDAOHibernate.buscarPorMatricula(matricula.trim());
            
            if (alunoExistente == null) {
                JOptionPane.showMessageDialog(this, "Aluno com matrícula '" + matricula + "' não encontrado no banco de dados.", 
                                             "Não Encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // PRIMEIRA MODAL: Informar que o aluno foi encontrado
            String mensagemEncontrado = String.format(
                "Aluno encontrado:\n\n" +
                "Matrícula: %s\n" +
                "Nome: %s\n" +
                "Data Nascimento: %s\n" +
                "Telefone: %s\n" +
                "CPF: %s\n\n" +
                "Deseja prosseguir com a edição?",
                alunoExistente.getMatricula(),
                alunoExistente.getNome(),
                alunoExistente.getDataNascimento() != null ? sdf.format(alunoExistente.getDataNascimento()) : "Não informado",
                alunoExistente.getTelefone() != null ? alunoExistente.getTelefone() : "Não informado",
                alunoExistente.getCpf() != null ? alunoExistente.getCpf() : "Não informado"
            );
            
            int confirmarEdicao = JOptionPane.showConfirmDialog(this, 
                                                              mensagemEncontrado,
                                                              "Aluno Encontrado", 
                                                              JOptionPane.YES_NO_OPTION,
                                                              JOptionPane.INFORMATION_MESSAGE);
            
            if (confirmarEdicao != JOptionPane.YES_OPTION) {
                return; // Usuário cancelou
            }

            // PREENCHER O FORMULÁRIO com os dados do aluno encontrado
            txtMatricula.setText(alunoExistente.getMatricula());
            txtMatricula.setEditable(false);
            txtNome.setText(alunoExistente.getNome());
            if (alunoExistente.getDataNascimento() != null) {
                txtDataNascimento.setText(sdf.format(alunoExistente.getDataNascimento()));
            }
            if (alunoExistente.getTelefone() != null) {
                txtTelefone.setText(alunoExistente.getTelefone());
            }
            if (alunoExistente.getCpf() != null) {
                txtCpf.setText(alunoExistente.getCpf());
            }

            // ENTRAR NO MODO DE EDIÇÃO
            entrarModoEdicao(matricula.trim());
            
            JOptionPane.showMessageDialog(this, 
                "Os dados do aluno foram carregados nos campos acima.\n" +
                "Edite as informações desejadas e clique em 'Salvar Alterações' para confirmar.", 
                "Modo de Edição", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar aluno: " + ex.getMessage(), 
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // NOVO MÉTODO: Entrar no modo de edição
    private void entrarModoEdicao(String matricula) {
        modoEdicao = true;
        matriculaEmEdicao = matricula;
        
        // OBTER O PAINEL DE BOTÕES
        Container panelBotoes = btnAdicionar.getParent();
        
        // REMOVER TODOS OS BOTÕES
        panelBotoes.removeAll();
        
        // CRIAR APENAS OS 2 BOTÕES DE EDIÇÃO
        btnSalvarAlteracoes = new JButton("Salvar Alterações");
        btnCancelarEdicao = new JButton("Cancelar");
        
        btnSalvarAlteracoes.addActionListener(this::salvarAlteracoes);
        btnCancelarEdicao.addActionListener(this::cancelarEdicao);
        
        // ADICIONAR APENAS ESTES 2 BOTÕES
        panelBotoes.add(btnSalvarAlteracoes);
        panelBotoes.add(btnCancelarEdicao);
        
        // ATUALIZAR A INTERFACE
        panelBotoes.revalidate();
        panelBotoes.repaint();
    }

    // NOVO MÉTODO: Salvar alterações
    private void salvarAlteracoes(ActionEvent e) {
        try {
            Aluno alunoAtualizado = criarAlunoPeloFormulario();
            
            // Atualizar no banco usando Hibernate
            alunoDAOHibernate.atualizar(alunoAtualizado);
            
            // Atualizar na lista em memória
            for (int i = 0; i < listaAlunos.size(); i++) {
                if (listaAlunos.get(i).getMatricula().equals(matriculaEmEdicao)) {
                    listaAlunos.set(i, alunoAtualizado);
                    break;
                }
            }
            
            // Atualizar o arquivo CSV
            salvarParaCSV();
            
            JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso!", 
                                         "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            // Sair do modo de edição
            sairModoEdicao();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar aluno: " + ex.getMessage(), 
                                         "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // NOVO MÉTODO: Cancelar edição
    private void cancelarEdicao(ActionEvent e) {
        int resposta = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja cancelar a edição?\nTodas as alterações serão perdidas.", 
            "Confirmar Cancelamento", 
            JOptionPane.YES_NO_OPTION);
        
        if (resposta == JOptionPane.YES_OPTION) {
            sairModoEdicao();
        }
    }

    // MÉTODO MODIFICADO: Sair do modo de edição
    private void sairModoEdicao() {
        modoEdicao = false;
        matriculaEmEdicao = "";
        
        // OBTER O PAINEL DE BOTÕES
        Container panelBotoes = btnSalvarAlteracoes.getParent();
        
        // REMOVER OS BOTÕES DE EDIÇÃO
        panelBotoes.removeAll();
        
        // RECRIAR TODOS OS BOTÕES ORIGINAIS
        btnAdicionar = new JButton("Adicionar");
        btnBuscar = new JButton("Pesquisar Aluno");
        btnRemover = new JButton("Remover Aluno");
        btnVerificarIdades = new JButton("Mais Novo/Velho");
        btnInserirPosicao = new JButton("Inserir em Posição");
        btnListarTodos = new JButton("Listar Todos");
        JButton btnAtualizar = new JButton("Atualizar Aluno");
        
        // ADICIONAR TODOS OS BOTÕES DE VOLTA
        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnBuscar);
        panelBotoes.add(btnRemover);
        panelBotoes.add(btnAtualizar);
        panelBotoes.add(btnVerificarIdades);
        panelBotoes.add(btnInserirPosicao);
        panelBotoes.add(btnListarTodos);
        
        // RESTAURAR TODAS AS AÇÕES
        btnAdicionar.addActionListener(this::adicionarAluno);
        btnBuscar.addActionListener(this::buscarAluno);
        btnRemover.addActionListener(this::removerAluno);
        btnAtualizar.addActionListener(this::atualizarAluno);
        btnVerificarIdades.addActionListener(this::verificarIdades);
        btnInserirPosicao.addActionListener(this::inserirEmPosicao);
        btnListarTodos.addActionListener(e -> {
            if (listaAlunos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A lista de alunos está vazia.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            TabelaAlunosDialog dialogTabela = new TabelaAlunosDialog(this, listaAlunos);
            dialogTabela.setVisible(true);
        });
        
        // Limpar campos e restaurar estado
        limparCampos();
        txtMatricula.setEditable(true);
        
        // ATUALIZAR A INTERFACE
        panelBotoes.revalidate();
        panelBotoes.repaint();
    }

    // SIMPLIFICAR OS MÉTODOS adicionarAluno E buscarAluno
    private void adicionarAluno(ActionEvent e) {
        try {
            if (txtMatricula.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O campo matrícula é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // MODO NORMAL - ADICIONAR NOVO ALUNO
            verificarMatriculaDuplicada(txtMatricula.getText());
            Aluno novoAluno = criarAlunoPeloFormulario();
            
            // Salvar no banco de dados usando Hibernate
            alunoDAOHibernate.salvar(novoAluno);
            
            // Adicionar à lista em memória
            listaAlunos.add(novoAluno);

            JOptionPane.showMessageDialog(this, "Aluno " + novoAluno.getNome() + " adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            salvarParaCSV();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void buscarAluno(ActionEvent e) {
        // MODO NORMAL - BUSCAR ALUNO
        String matricula = JOptionPane.showInputDialog(this, "Digite a matrícula do aluno a ser buscado:");
        if (matricula == null || matricula.trim().isEmpty()) {
            return;
        }

        for (Aluno aluno : listaAlunos) {
            if (aluno.getMatricula().equalsIgnoreCase(matricula.trim())) {
                List<Aluno> resultadoBusca = new ArrayList<>();
                resultadoBusca.add(aluno);

                TabelaAlunosDialog dialog = new TabelaAlunosDialog(this, resultadoBusca);
                dialog.setVisible(true);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Aluno com matrícula '" + matricula + "' não encontrado.", "Não Encontrado", JOptionPane.WARNING_MESSAGE);
    }
    
    private void removerAluno(ActionEvent e) {
        String matricula = JOptionPane.showInputDialog(this, "Digite a matrícula do aluno a ser removido:");
        if (matricula == null || matricula.trim().isEmpty()) {
            return;
        }

        // 1. Encontrar o objeto Aluno que corresponde à matrícula
        Aluno alunoParaRemover = null;
        for (Aluno aluno : listaAlunos) {
            if (aluno.getMatricula().equalsIgnoreCase(matricula.trim())) {
                alunoParaRemover = aluno;
                break; // Encontrou o aluno, pode parar o loop
            }
        }

        // 2. Verificar se o aluno foi encontrado
        if (alunoParaRemover != null) {
            try {
                
                
                // 3. Instanciar e usar o DAO para remover o aluno da lista em memória
                AlunoDAO dao = new RemocaoAlunoDAO();
                this.listaAlunos = dao.removerAluno(this.listaAlunos, alunoParaRemover);

                // 4. Salvar a nova lista no arquivo CSV
                salvarParaCSV();

                // 5. Mostrar mensagem de sucesso
                JOptionPane.showMessageDialog(this, "Aluno " + alunoParaRemover.getNome() + " removido com sucesso da lista!", 
                                             "Remoção Concluída", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover aluno: " + ex.getMessage(), 
                                             "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            // Mensagem caso o aluno não seja encontrado
            JOptionPane.showMessageDialog(this, "Aluno com matrícula '" + matricula + "' não encontrado para remoção.", 
                                         "Não Encontrado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verificarIdades(ActionEvent e) {
        if (listaAlunos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A lista de alunos está vazia.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Aluno maisNovo = listaAlunos.get(0);
        Aluno maisVelho = listaAlunos.get(0);

        for (Aluno aluno : listaAlunos) {
            if (aluno.getDataNascimento().after(maisNovo.getDataNascimento())) {
                maisNovo = aluno;
            }
            if (aluno.getDataNascimento().before(maisVelho.getDataNascimento())) {
                maisVelho = aluno;
            }
        }

        // ALTERADO: Exibe o resultado na tabela
        List<Aluno> resultadoIdade = new ArrayList<>();
        resultadoIdade.add(maisVelho);
        // Adiciona o mais novo apenas se for uma pessoa diferente (para listas com > 1 aluno)
        if (!maisNovo.getMatricula().equals(maisVelho.getMatricula())) {
            resultadoIdade.add(maisNovo);
        }

        TabelaAlunosDialog dialog = new TabelaAlunosDialog(this, resultadoIdade);
        dialog.setVisible(true);
    }

    private void inserirEmPosicao(ActionEvent e) {
        try {
            if (txtMatricula.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os dados do aluno para inserir.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String posStr = JOptionPane.showInputDialog(this, "Digite a posição para inserir (ex: 1, 2, 3...):", "Inserir em Posição", JOptionPane.QUESTION_MESSAGE);
            if (posStr == null) {
                return;
            }

            int posicao = Integer.parseInt(posStr);
            if (posicao < 1 || posicao > listaAlunos.size() + 1) {
                JOptionPane.showMessageDialog(this, "Posição inválida. Deve ser entre 1 e " + (listaAlunos.size() + 1), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            verificarMatriculaDuplicada(txtMatricula.getText());
            Aluno novoAluno = criarAlunoPeloFormulario();
            listaAlunos.add(posicao - 1, novoAluno);

            // ALTERADO: Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Aluno " + novoAluno.getNome() + " inserido com sucesso na posição " + posicao + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            salvarParaCSV();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtMatricula.setText("");
        txtNome.setText("");
        txtIdadeCalculadaDisplay.setText("");
        txtDataNascimento.setValue(null);
        txtTelefone.setValue(null);
        txtCpf.setValue(null);
    }

    private Aluno criarAlunoPeloFormulario() throws ParseException, NumberFormatException {
        Aluno aluno = new Aluno();
        aluno.setMatricula(txtMatricula.getText().trim());
        aluno.setNome(txtNome.getText().trim());

        Date dataNasc = null;
        String dataNascStr = txtDataNascimento.getText();
        if (dataNascStr != null && !dataNascStr.contains("_") && dataNascStr.length() == 10) {
            dataNasc = sdf.parse(dataNascStr); // sdf.setLenient(false) já está no construtor
            
            Date hoje = new Date();
            if (dataNasc.after(hoje)) {
                throw new ParseException("Data de nascimento não pode ser posterior à data atual. " +
                                   "Data informada: " + dataNascStr + 
                                   ", Data atual: " + sdf.format(hoje), 0);
            }
            
            aluno.setDataNascimento(dataNasc);
            aluno.setIdade(calcularIdade(dataNasc));
        } else {
            throw new ParseException("Data de nascimento incompleta ou inválida.", 0);
        }

        aluno.setTelefone(txtTelefone.getText());
        aluno.setCpf(txtCpf.getText());
        return aluno;
    }

    private void verificarMatriculaDuplicada(String matricula) throws MatriculaDuplicadaException {
        for (Aluno a : listaAlunos) {
            if (a.getMatricula().equalsIgnoreCase(matricula)) {
                throw new MatriculaDuplicadaException("Erro: A matrícula '" + matricula + "' já existe!");
            }
        }
    }

    private void salvarParaCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO))) {
            for (Aluno aluno : listaAlunos) {
                String dataFormatada = sdf.format(aluno.getDataNascimento());
                String linhaCSV = String.join(";",
                        aluno.getMatricula(),
                        aluno.getNome(),
                        String.valueOf(aluno.getIdade()),
                        dataFormatada,
                        aluno.getTelefone(),
                        aluno.getCpf()
                );
                writer.println(linhaCSV);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo: " + e.getMessage(), "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDeCSV() {
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";", -1);
                if (dados.length == 6) {
                    Aluno aluno = new Aluno();
                    aluno.setMatricula(dados[0]);
                    aluno.setNome(dados[1]);

                    // MODIFICAÇÃO: Calcula a idade com base na data de nascimento carregada
                    Date dataNascCarregada = null;
                    if (!dados[3].trim().isEmpty()) {
                        try {
                            dataNascCarregada = sdf.parse(dados[3].trim());
                            aluno.setDataNascimento(dataNascCarregada);
                            aluno.setIdade(calcularIdade(dataNascCarregada));
                        } catch (ParseException pe) {
                            System.err.println("Erro ao parsear data do CSV para aluno " + dados[0] + ": " + dados[3]);
                            // Se a data for inválida, tenta usar a idade do CSV como fallback
                            try {
                                aluno.setIdade(Integer.parseInt(dados[2].trim()));
                            } catch (NumberFormatException nfe) {
                                aluno.setIdade(0); // Ou algum valor padrão
                            }
                        }
                    } else {
                        // Se não há data de nascimento no CSV, tenta usar a idade do CSV
                        try {
                            aluno.setIdade(Integer.parseInt(dados[2].trim()));
                        } catch (NumberFormatException nfe) {
                            aluno.setIdade(0); // Ou algum valor padrão
                        }
                    }

                    aluno.setTelefone(dados[4]);
                    aluno.setCpf(dados[5]);
                    listaAlunos.add(aluno);
                }
            }
        } catch (IOException | NumberFormatException e) { // ParseException é tratada dentro do loop
            JOptionPane.showMessageDialog(this, "Erro ao carregar o arquivo CSV: " + e.getMessage() + "\nVerifique o formato do arquivo.", "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AlunoForm().setVisible(true);
        });
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 774, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 364, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            // Configurar o look and feel
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Iniciar o aplicativo
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        System.out.println("Iniciando aplicativo...");
                        new AlunoForm().setVisible(true);
                    } catch (Exception e) {
                        System.err.println("ERRO AO INICIAR APLICATIVO: " + e.getMessage());
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, 
                            "Erro ao iniciar o aplicativo: " + e.getMessage(), 
                            "Erro Fatal", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } catch (Exception ex) {
            System.err.println("ERRO NA INICIALIZAÇÃO: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

   
    }