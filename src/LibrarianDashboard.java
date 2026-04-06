// File: src/LibrarianDashboard.java
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibrarianDashboard extends JFrame {

    private String librarianName;
    private DefaultTableModel bookModel;
    private DefaultTableModel issuedModel;
    private JTable bookTable;
    private JTable issuedTable;

    static final Color BG     = new Color(18, 32, 47);
    static final Color HEADER = new Color(15, 45, 55);
    static final Color TEAL   = new Color(30, 180, 160);
    static final Color CARD   = new Color(20, 50, 60);
    static final Color WHITE  = new Color(240, 240, 245);
    static final Color GRAY   = new Color(160, 175, 190);
    static final Color GREEN  = new Color(46, 160, 100);
    static final Color RED    = new Color(200, 60, 60);
    static final Color ACCENT = new Color(100, 160, 220);
    static final Color ORANGE = new Color(220, 140, 40);

    public LibrarianDashboard(String name) {
        this.librarianName = name;
        setTitle("Librarian Dashboard - Library Management System");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER);
        header.setPreferredSize(new Dimension(1100, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TEAL));

        JLabel titleLbl = new JLabel("  Library Management - Librarian Panel");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(TEAL);
        header.add(titleLbl, BorderLayout.WEST);

        JPanel rightH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        rightH.setOpaque(false);
        JLabel userLbl = new JLabel("Librarian: " + librarianName);
        userLbl.setForeground(WHITE);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightH.add(userLbl);

        JButton logoutBtn = smallBtn("Logout", RED);
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        rightH.add(logoutBtn);
        header.add(rightH, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(15, 45, 55));
        tabs.setForeground(WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("Add Student", createAddStudentTab());
        tabs.addTab("Add Book", createAddBookTab());
        tabs.addTab("View / Delete Books", createViewBooksTab());
        tabs.addTab("Issue Book", createIssueBookTab());
        tabs.addTab("Issued Books", createViewIssuedTab());

        // Bright tab labels
        String[] tabNames = {
            "  Add Student  ",
            "  Add Book  ",
            "  View / Delete Books  ",
            "  Issue Book  ",
            "  Issued Books  "
        };
        for (int i = 0; i < tabs.getTabCount(); i++) {
            JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 3));
            tabPanel.setOpaque(false);
            JLabel tabLabel = new JLabel(tabNames[i]);
            tabLabel.setForeground(new Color(80, 220, 200));
            tabLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            tabPanel.add(tabLabel);
            tabs.setTabComponentAt(i, tabPanel);
        }

        add(tabs, BorderLayout.CENTER);
    }

    // ===================== ADD STUDENT TAB =====================
    private JPanel createAddStudentTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(30, 90, 100), 1, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Add New Student");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(TEAL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        gbc.gridwidth = 1;

        JTextField sidField   = styledField("e.g. STU001");
        JTextField nameField  = styledField("Full Name");
        JTextField emailField = styledField("Email address");
        JTextField deptField  = styledField("e.g. Computer Science");
        JTextField phoneField = styledField("Phone number");

        addRow(card, gbc, "Student ID:", sidField, 1);
        addRow(card, gbc, "Full Name:", nameField, 2);
        addRow(card, gbc, "Email:", emailField, 3);
        addRow(card, gbc, "Department:", deptField, 4);
        addRow(card, gbc, "Phone:", phoneField, 5);

        JButton addBtn = styledBtn("Add Student", GREEN);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        card.add(addBtn, gbc);

        JLabel statusLbl = new JLabel("", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gbc.gridy = 7;
        card.add(statusLbl, gbc);

        panel.add(card);

        addBtn.addActionListener(e -> {
            String sid   = sidField.getText().trim();
            String sname = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dept  = deptField.getText().trim();
            String phone = phoneField.getText().trim();

            if (sid.isEmpty() || sname.isEmpty()) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Student ID and Name are required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO students (student_id, full_name, email, department, phone) VALUES (?,?,?,?,?)"
                );
                ps.setString(1, sid); ps.setString(2, sname);
                ps.setString(3, email); ps.setString(4, dept); ps.setString(5, phone);
                ps.executeUpdate();
                statusLbl.setForeground(GREEN);
                statusLbl.setText("Student added successfully!");
                sidField.setText(""); nameField.setText(""); emailField.setText("");
                deptField.setText(""); phoneField.setText("");
            } catch (SQLIntegrityConstraintViolationException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Student ID already exists!");
            } catch (SQLException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ===================== ADD BOOK TAB =====================
    private JPanel createAddBookTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(30, 90, 100), 1, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Add New Book to Library");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(TEAL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        gbc.gridwidth = 1;

        JTextField bookTitle    = styledField("Enter book title");
        JTextField bookAuthor   = styledField("Enter author name");
        JTextField bookISBN     = styledField("Enter ISBN");
        JTextField bookCategory = styledField("e.g. Programming");
        JTextField bookQty      = styledField("Number of copies");

        addRow(card, gbc, "Book Title:", bookTitle, 1);
        addRow(card, gbc, "Author:", bookAuthor, 2);
        addRow(card, gbc, "ISBN:", bookISBN, 3);
        addRow(card, gbc, "Category:", bookCategory, 4);
        addRow(card, gbc, "Quantity:", bookQty, 5);

        JButton addBtn = styledBtn("Add Book to Library", GREEN);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        card.add(addBtn, gbc);

        JLabel statusLbl = new JLabel("", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gbc.gridy = 7;
        card.add(statusLbl, gbc);

        panel.add(card);

        addBtn.addActionListener(e -> {
            String t    = bookTitle.getText().trim();
            String a    = bookAuthor.getText().trim();
            String isbn = bookISBN.getText().trim();
            String cat  = bookCategory.getText().trim();
            int qty = 1;
            try { qty = Integer.parseInt(bookQty.getText().trim()); } catch (Exception ignored) {}

            if (t.isEmpty() || a.isEmpty()) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Title and Author are required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO books (title, author, isbn, category, quantity, available) VALUES (?,?,?,?,?,?)"
                );
                ps.setString(1, t); ps.setString(2, a); ps.setString(3, isbn);
                ps.setString(4, cat); ps.setInt(5, qty); ps.setInt(6, qty);
                ps.executeUpdate();
                statusLbl.setForeground(GREEN);
                statusLbl.setText("Book added successfully!");
                bookTitle.setText(""); bookAuthor.setText(""); bookISBN.setText("");
                bookCategory.setText(""); bookQty.setText("");
            } catch (SQLException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ===================== VIEW/DELETE BOOKS TAB =====================
    private JPanel createViewBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setBackground(BG);

        JTextField searchField = styledField("Search by title or author...");
        searchField.setPreferredSize(new Dimension(280, 35));
        JButton searchBtn  = styledBtn("Search", TEAL);
        JButton refreshBtn = styledBtn("Refresh", new Color(60, 90, 120));
        JButton deleteBtn  = styledBtn("Delete Selected", RED);

        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(refreshBtn);
        topBar.add(deleteBtn);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Author", "ISBN", "Category", "Total", "Available"};
        bookModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookTable = styledTable(bookModel);
        JScrollPane scroll = new JScrollPane(bookTable);
        scroll.getViewport().setBackground(new Color(20, 38, 55));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(35, 90, 100)));
        panel.add(scroll, BorderLayout.CENTER);

        loadBooks(null);

        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadBooks(null); });

        deleteBtn.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a book to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) bookModel.getValueAt(row, 0);
            String bookName = (String) bookModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete book: \"" + bookName + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBooks(null);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    // ===================== ISSUE BOOK TAB =====================
    private JPanel createIssueBookTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(30, 90, 100), 1, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Issue Book to Student");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(ORANGE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        gbc.gridwidth = 1;

        // Fields
        JTextField studentIdField   = styledField("e.g. STU001");
        JTextField studentNameField = styledField("Auto-filled from Student ID");
        studentNameField.setEditable(false);
        studentNameField.setBackground(new Color(15, 40, 50));

        JTextField bookIdField   = styledField("e.g. 1, 2, 3...");
        JTextField bookNameField = styledField("Auto-filled from Book ID");
        bookNameField.setEditable(false);
        bookNameField.setBackground(new Color(15, 40, 50));

        // Today's date auto-filled
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String defaultDue = LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        JTextField issueDateField = styledField("YYYY-MM-DD");
        issueDateField.setText(today);
        JTextField dueDateField   = styledField("YYYY-MM-DD");
        dueDateField.setText(defaultDue);

        addRow(card, gbc, "Student ID:", studentIdField, 1);
        addRow(card, gbc, "Student Name:", studentNameField, 2);
        addRow(card, gbc, "Book ID:", bookIdField, 3);
        addRow(card, gbc, "Book Name:", bookNameField, 4);
        addRow(card, gbc, "Issue Date:", issueDateField, 5);
        addRow(card, gbc, "Due Date:", dueDateField, 6);

        // Hint label
        JLabel hint = new JLabel("Tip: Enter Student ID and Book ID — names will auto-fill");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(GRAY);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        card.add(hint, gbc);

        JButton issueBtn = styledBtn("Issue Book", ORANGE);
        gbc.gridy = 8; gbc.insets = new Insets(15, 8, 5, 8);
        card.add(issueBtn, gbc);

        JLabel statusLbl = new JLabel("", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gbc.gridy = 9;
        card.add(statusLbl, gbc);

        panel.add(card);

        // Auto-fill student name when student ID is entered
        studentIdField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String sid = studentIdField.getText().trim();
                if (!sid.isEmpty()) {
                    try (Connection conn = DBConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT full_name FROM students WHERE student_id=?"
                        );
                        ps.setString(1, sid);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            studentNameField.setText(rs.getString("full_name"));
                            studentNameField.setForeground(GREEN);
                        } else {
                            studentNameField.setText("Student not found!");
                            studentNameField.setForeground(RED);
                        }
                    } catch (SQLException ex) { ex.printStackTrace(); }
                }
            }
        });

        // Auto-fill book name when book ID is entered
        bookIdField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String bid = bookIdField.getText().trim();
                if (!bid.isEmpty()) {
                    try (Connection conn = DBConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT title, available FROM books WHERE id=?"
                        );
                        ps.setInt(1, Integer.parseInt(bid));
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            int avail = rs.getInt("available");
                            bookNameField.setText(rs.getString("title"));
                            if (avail > 0) {
                                bookNameField.setForeground(GREEN);
                            } else {
                                bookNameField.setText(rs.getString("title") + " (NOT AVAILABLE)");
                                bookNameField.setForeground(RED);
                            }
                        } else {
                            bookNameField.setText("Book not found!");
                            bookNameField.setForeground(RED);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        });

        // Issue book action
        issueBtn.addActionListener(e -> {
            String sid       = studentIdField.getText().trim();
            String sname     = studentNameField.getText().trim();
            String bidStr    = bookIdField.getText().trim();
            String bname     = bookNameField.getText().trim();
            String issueDate = issueDateField.getText().trim();
            String dueDate   = dueDateField.getText().trim();

            if (sid.isEmpty() || bidStr.isEmpty() || issueDate.isEmpty() || dueDate.isEmpty()) {
                statusLbl.setForeground(RED);
                statusLbl.setText("All fields are required!");
                return;
            }
            if (sname.contains("not found") || bname.contains("not found") || bname.contains("NOT AVAILABLE")) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Invalid student or book not available!");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                int bookId = Integer.parseInt(bidStr);

                // Check availability
                PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id=?");
                check.setInt(1, bookId);
                ResultSet rs = check.executeQuery();
                if (rs.next() && rs.getInt("available") <= 0) {
                    statusLbl.setForeground(RED);
                    statusLbl.setText("Sorry! This book is not available right now.");
                    return;
                }

                // Insert issued record
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO issued_books (student_id, student_name, book_id, book_name, issue_date, due_date) VALUES (?,?,?,?,?,?)"
                );
                ps.setString(1, sid); ps.setString(2, sname);
                ps.setInt(3, bookId); ps.setString(4, bname);
                ps.setString(5, issueDate); ps.setString(6, dueDate);
                ps.executeUpdate();

                // Reduce available count
                PreparedStatement update = conn.prepareStatement(
                    "UPDATE books SET available = available - 1 WHERE id=?"
                );
                update.setInt(1, bookId);
                update.executeUpdate();

                statusLbl.setForeground(GREEN);
                statusLbl.setText("Book issued successfully to " + sname + "!");
                studentIdField.setText(""); studentNameField.setText("");
                bookIdField.setText(""); bookNameField.setText("");
                issueDateField.setText(today); dueDateField.setText(defaultDue);

            } catch (SQLException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ===================== VIEW ISSUED BOOKS TAB =====================
    private JPanel createViewIssuedTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setBackground(BG);

        JTextField searchField = styledField("Search by student name or ID...");
        searchField.setPreferredSize(new Dimension(260, 35));

        JButton searchBtn   = styledBtn("Search", TEAL);
        JButton refreshBtn  = styledBtn("Refresh", new Color(60, 90, 120));
        JButton returnBtn   = styledBtn("Mark as Returned", GREEN);

        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(refreshBtn);
        topBar.add(returnBtn);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Student ID", "Student Name", "Book Name", "Issue Date", "Due Date", "Status"};
        issuedModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        issuedTable = new JTable(issuedModel) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    String status = (String) getModel().getValueAt(row, 6);
                    if ("returned".equals(status)) {
                        c.setBackground(new Color(20, 55, 30));
                    } else {
                        c.setBackground(new Color(55, 25, 20));
                    }
                    c.setForeground(WHITE);
                }
                return c;
            }
        };
        issuedTable.setForeground(WHITE);
        issuedTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        issuedTable.setRowHeight(30);
        issuedTable.setGridColor(new Color(35, 65, 85));
        issuedTable.setSelectionBackground(new Color(30, 90, 100));
        issuedTable.setSelectionForeground(WHITE);
        issuedTable.getTableHeader().setBackground(HEADER);
        issuedTable.getTableHeader().setForeground(TEAL);
        issuedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(issuedTable);
        scroll.getViewport().setBackground(new Color(20, 38, 55));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(35, 90, 100)));
        panel.add(scroll, BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legend.setBackground(BG);
        JLabel l1 = new JLabel("  Issued (Red background)");
        l1.setForeground(new Color(255, 120, 100));
        l1.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        JLabel l2 = new JLabel("  Returned (Green background)");
        l2.setForeground(new Color(100, 200, 120));
        l2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        legend.add(l1); legend.add(l2);
        panel.add(legend, BorderLayout.SOUTH);

        loadIssuedBooks(null);

        searchBtn.addActionListener(e -> loadIssuedBooks(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadIssuedBooks(null); });

        // Mark as returned
        returnBtn.addActionListener(e -> {
            int row = issuedTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a record to mark as returned!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String status = (String) issuedModel.getValueAt(row, 6);
            if ("returned".equals(status)) {
                JOptionPane.showMessageDialog(this, "This book is already returned!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int id = (int) issuedModel.getValueAt(row, 0);
            String studentName = (String) issuedModel.getValueAt(row, 2);
            String bookName = (String) issuedModel.getValueAt(row, 3);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Mark book \"" + bookName + "\" as returned by " + studentName + "?",
                "Confirm Return", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    // Update issued record
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE issued_books SET status='returned', return_date=? WHERE id=?"
                    );
                    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    ps.setString(1, today);
                    ps.setInt(2, id);
                    ps.executeUpdate();

                    // Get book_id and increase available count
                    PreparedStatement getBook = conn.prepareStatement(
                        "SELECT book_id FROM issued_books WHERE id=?"
                    );
                    getBook.setInt(1, id);
                    ResultSet rs = getBook.executeQuery();
                    if (rs.next()) {
                        int bookId = rs.getInt("book_id");
                        PreparedStatement update = conn.prepareStatement(
                            "UPDATE books SET available = available + 1 WHERE id=?"
                        );
                        update.setInt(1, bookId);
                        update.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Book marked as returned!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadIssuedBooks(null);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void loadIssuedBooks(String search) {
        issuedModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps;
            if (search == null || search.isEmpty()) {
                ps = conn.prepareStatement("SELECT * FROM issued_books ORDER BY issue_date DESC");
            } else {
                ps = conn.prepareStatement(
                    "SELECT * FROM issued_books WHERE student_name LIKE ? OR student_id LIKE ?"
                );
                ps.setString(1, "%" + search + "%");
                ps.setString(2, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                issuedModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("student_name"),
                    rs.getString("book_name"),
                    rs.getString("issue_date"),
                    rs.getString("due_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadBooks(String search) {
        bookModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps;
            if (search == null || search.isEmpty()) {
                ps = conn.prepareStatement("SELECT * FROM books ORDER BY added_at DESC");
            } else {
                ps = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?");
                ps.setString(1, "%" + search + "%");
                ps.setString(2, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                    rs.getString("isbn"), rs.getString("category"),
                    rs.getInt("quantity"), rs.getInt("available")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ============= HELPERS =============
    private void addRow(JPanel p, GridBagConstraints g, String label, JTextField field, int row) {
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = row;
        g.insets = new Insets(8, 8, 8, 8);
        p.add(makeLabel(label), g);
        g.gridx = 1; g.ipadx = 150;
        p.add(field, g);
        g.ipadx = 0;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(GRAY);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JTextField styledField(String tip) {
        JTextField f = new JTextField(15);
        f.setBackground(new Color(25, 55, 65));
        f.setForeground(WHITE);
        f.setCaretColor(TEAL);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(30, 90, 100), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        f.setToolTipText(tip);
        return f;
    }

    private JButton styledBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private JButton smallBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(new Color(20, 38, 55));
        table.setForeground(WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(35, 65, 85));
        table.setSelectionBackground(new Color(30, 90, 100));
        table.setSelectionForeground(WHITE);
        table.getTableHeader().setBackground(HEADER);
        table.getTableHeader().setForeground(TEAL);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        return table;
    }
}