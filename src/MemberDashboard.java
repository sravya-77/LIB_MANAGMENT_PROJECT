import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class MemberDashboard extends JFrame {

    private String memberName;
    private String username;
    private DefaultTableModel bookModel;
    private JTable bookTable;
    private JLabel totalBooksLbl;
    private JLabel availableBooksLbl;

    static final Color BG      = new Color(18, 32, 47);
    static final Color HEADER  = new Color(28, 20, 55);
    static final Color PURPLE  = new Color(140, 100, 220);
    static final Color LIGHT_P = new Color(180, 150, 255);
    static final Color CARD    = new Color(30, 22, 60);
    static final Color WHITE   = new Color(240, 240, 245);
    static final Color GRAY    = new Color(160, 175, 190);
    static final Color GREEN   = new Color(46, 160, 100);
    static final Color RED     = new Color(200, 60, 60);

    public MemberDashboard(String name, String username) {
        this.memberName = name;
        this.username = username;
        setTitle("Member Dashboard - Library Management System");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER);
        header.setPreferredSize(new Dimension(900, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PURPLE));

        JLabel titleLbl = new JLabel("  📚 Library Management - Member Portal");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(LIGHT_P);
        header.add(titleLbl, BorderLayout.WEST);

        JPanel rightH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        rightH.setOpaque(false);
        JLabel userLbl = new JLabel("👤 " + memberName);
        userLbl.setForeground(WHITE);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightH.add(userLbl);

        JButton logoutBtn = makeBtn("Logout", RED);
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        rightH.add(logoutBtn);
        header.add(rightH, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Stats Row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        statsRow.setBackground(new Color(22, 15, 45));
        statsRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 40, 100)));

        totalBooksLbl     = statLabel("📦 Total Books: --");
        availableBooksLbl = statLabel("✅ Available: --");

        statsRow.add(totalBooksLbl);
        statsRow.add(new JSeparator(SwingConstants.VERTICAL));
        statsRow.add(availableBooksLbl);
        add(statsRow, BorderLayout.BEFORE_FIRST_LINE);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchPanel.setBackground(BG);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

        JLabel searchLbl = new JLabel("🔍 Search Book:");
        searchLbl.setForeground(GRAY);
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextField searchField = new JTextField(25);
        searchField.setBackground(new Color(30, 20, 60));
        searchField.setForeground(WHITE);
        searchField.setCaretColor(PURPLE);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 50, 130), 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        JButton searchBtn  = makeBtn("Search", PURPLE);
        JButton showAllBtn = makeBtn("Show All", new Color(60, 80, 120));

        String[] filterOpts = {"All Categories", "Programming", "Computer Science", "Database", "Systems", "Networks", "Science", "Math"};
        JComboBox<String> categoryFilter = new JComboBox<>(filterOpts);
        categoryFilter.setBackground(new Color(30, 20, 60));
        categoryFilter.setForeground(WHITE);
        categoryFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(categoryFilter);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        // Center area
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "📖 Title", "✍ Author", "ISBN", "📁 Category", "Total", "Available"};
        bookModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookTable = new JTable(bookModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    int available = (int) getModel().getValueAt(row, 6);
                    c.setBackground(available > 0 ? new Color(18, 38, 55) : new Color(40, 18, 18));
                }
                return c;
            }
        };
        bookTable.setForeground(WHITE);
        bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookTable.setRowHeight(32);
        bookTable.setGridColor(new Color(40, 25, 70));
        bookTable.setSelectionBackground(new Color(80, 50, 150));
        bookTable.setSelectionForeground(WHITE);
        bookTable.getTableHeader().setBackground(HEADER);
        bookTable.getTableHeader().setForeground(LIGHT_P);
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookTable.setShowVerticalLines(true);

        JScrollPane scroll = new JScrollPane(bookTable);
        scroll.getViewport().setBackground(new Color(18, 28, 45));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 40, 100)));

        centerPanel.add(scroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Footer hint
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(15, 10, 30));
        JLabel hint = new JLabel("💡 Rows highlighted in red = Book not available  |  Green = Available");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(GRAY);
        footer.add(hint);
        add(footer, BorderLayout.SOUTH);

        // Load
        loadBooks(null, null);

        // Actions
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            String cat = categoryFilter.getSelectedIndex() == 0 ? null : (String) categoryFilter.getSelectedItem();
            loadBooks(query.isEmpty() ? null : query, cat);
        });

        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            loadBooks(null, null);
        });

        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) searchBtn.doClick();
            }
        });

        // add stats panel to header area
        add(statsRow, BorderLayout.BEFORE_FIRST_LINE);
    }

    private void loadBooks(String search, String category) {
        bookModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
            if (search != null && !search.isEmpty())
                sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
            if (category != null && !category.isEmpty())
                sql.append(" AND category = ?");
            sql.append(" ORDER BY title");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (search != null && !search.isEmpty()) {
                ps.setString(idx++, "%" + search + "%");
                ps.setString(idx++, "%" + search + "%");
                ps.setString(idx++, "%" + search + "%");
            }
            if (category != null && !category.isEmpty())
                ps.setString(idx, category);

            ResultSet rs = ps.executeQuery();
            int total = 0, avail = 0;
            while (rs.next()) {
                int qty = rs.getInt("quantity");
                int av  = rs.getInt("available");
                total += qty; avail += av;
                bookModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                    rs.getString("isbn"), rs.getString("category"), qty, av
                });
            }
            totalBooksLbl.setText("📦 Total Books: " + total);
            availableBooksLbl.setText("✅ Available: " + avail);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JLabel statLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(LIGHT_P);
        l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return l;
    }

    private JButton makeBtn(String text, Color color) {
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
}