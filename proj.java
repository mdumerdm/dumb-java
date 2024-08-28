import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Finance extends JFrame {
    private Connection conn;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField typeField;
    private JTextField amountField;
    private JTextField descriptionField;
    private JTextField currencyField;
    private JTextField dateField;
    private String userEmail;

    public Finance() {
        // JDBC URL, username, and password
        String url = "jdbc:mysql://localhost:3306/findb";
        String username = "root";
        String password = "1122";

        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Initialize the GUI components
        initUI();
    }

    private void initUI() {
        setTitle("Finance Application");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Instructions for the user
        JPanel instructionPanel = new JPanel(new GridLayout(3, 1));
        instructionPanel.add(new JLabel("Welcome to Finance Application!"));
        instructionPanel.add(new JLabel("Please select 'Sign In' if you already have an account."));
        instructionPanel.add(new JLabel("Or select 'Sign Up' to create a new account."));

        // Login/Signup Panel
        JPanel loginPanel = new JPanel(new GridLayout(1, 2));
        
        JButton signinButton = new JButton("Sign In");
        signinButton.addActionListener(e -> signinUI());
        loginPanel.add(signinButton);
        
        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> signupUI());
        loginPanel.add(signupButton);

        add(instructionPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
    }

    private void signinUI() {
        JPanel signinPanel = new JPanel(new GridLayout(3, 2));
        signinPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        signinPanel.add(emailField);

        signinPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        signinPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> signin());
        signinPanel.add(loginButton);

        getContentPane().removeAll();
        add(signinPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void signupUI() {
        JPanel signupPanel = new JPanel(new GridLayout(4, 2));
        signupPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        signupPanel.add(nameField);

        signupPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        signupPanel.add(emailField);

        signupPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        signupPanel.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> signup());
        signupPanel.add(registerButton);

        getContentPane().removeAll();
        add(signupPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void mainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(3, 1));
        menuPanel.add(new JLabel("Welcome!"));

        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.addActionListener(e -> addTransactionUI());
        menuPanel.add(addTransactionButton);

        JButton viewHistoryButton = new JButton("View Transaction History");
        viewHistoryButton.addActionListener(e -> viewTransactionHistory());
        menuPanel.add(viewHistoryButton);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));
        menuPanel.add(quitButton);

        getContentPane().removeAll();
        add(menuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void signin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String selectSql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userEmail = email;
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    mainMenu();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void signup() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        String insertSql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            int rowsAdded = stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account created successfully. You can now login.");
            showLoginSignupOptions(); // Show options for login and signup
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoginSignupOptions() {
        JPanel optionPanel = new JPanel(new GridLayout(2, 1));
        
        JButton signinButton = new JButton("Sign In");
        signinButton.addActionListener(e -> signinUI());
        optionPanel.add(signinButton);
        
        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> signupUI());
        optionPanel.add(signupButton);

        getContentPane().removeAll();
        add(optionPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addTransactionUI() {
        JPanel transactionPanel = new JPanel(new GridLayout(6, 2));
        transactionPanel.add(new JLabel("Type:"));
        typeField = new JTextField();
        transactionPanel.add(typeField);

        transactionPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        transactionPanel.add(amountField);

        transactionPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        transactionPanel.add(descriptionField);

        transactionPanel.add(new JLabel("Currency:"));
        currencyField = new JTextField();
        transactionPanel.add(currencyField);

        transactionPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        transactionPanel.add(dateField);

        JButton addButton = new JButton("Add Transaction");
        addButton.addActionListener(e -> addTransaction());
        transactionPanel.add(addButton);

        getContentPane().removeAll();
        add(transactionPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addTransaction() {
        String type = typeField.getText();
        double amount = Double.parseDouble(amountField.getText());
        String description = descriptionField.getText();
        String currency = currencyField.getText();
        String date = dateField.getText();

        String insertSql = "INSERT INTO transactions (type, amount, description, currency, date, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, type);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);
            stmt.setString(4, currency);
            stmt.setString(5, date);
            stmt.setString(6, userEmail);
            int TAdded = stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, TAdded + " Transaction successfull ");
            mainMenu();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewTransactionHistory() {
        String selectSql = "SELECT * FROM transactions WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
    
                // Append transactions to the JTextArea
                while (rs.next()) {
                    textArea.append("Transaction ID: " + rs.getInt("T-ID") + "\n");
                    textArea.append("Type: " + rs.getString("type") + "\n");
                    textArea.append("Amount: " + rs.getDouble("amount") + "\n");
                    textArea.append("Description: " + rs.getString("description") + "\n");
                    textArea.append("Currency: " + rs.getString("currency") + "\n");
                    textArea.append("Date: " + rs.getString("date") + "\n\n");
                }
    
                // Set preferred size for the JTextArea
                textArea.setRows(10); // Adjust the number of rows as needed
                textArea.setColumns(40); // Adjust the number of columns as needed
    
                // Wrap JTextArea with JScrollPane
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    
                JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.INFORMATION_MESSAGE);
                mainMenu();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Finance app = new Finance();
            app.setVisible(true);
        });
    }
}