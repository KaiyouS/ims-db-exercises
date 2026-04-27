/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package final_exam.dialogs.purchase;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import final_exam.utils.SQLConfig;
import table.cell.AlternatingRowColorRenderer;

public class SavePurchaseDialog extends javax.swing.JDialog {

    public SavePurchaseDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadItems();
        loadSuppliers();
        loadTable();
    }
    
    private void loadTable() {
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Item", "Quantity", "Unit Price", "Amount", "Notes" }, 0);
        applyCustomStyles(ordersTable, pane, scroll);
        ordersTable.setModel(tableModel);
        
        setColumnWidths(ordersTable, 0, 200);
        setColumnWidths(ordersTable, 1, 150);
        setColumnWidths(ordersTable, 2, 150);
        setColumnWidths(ordersTable, 3, 150);
        customizeTableHeader(ordersTable);
        ordersTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
        ordersTable.setRowSelectionAllowed(false);
        ordersTable.setColumnSelectionAllowed(false);
        ordersTable.setCellSelectionEnabled(false);
        ordersTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private double calculateTotalAmount() {
        DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
        double totalAmount = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            totalAmount += (double) model.getValueAt(i, 3);
        }
        return totalAmount;
    }
    
    private void applyCustomStyles(JTable table, JPanel panel, JScrollPane scrollPane) {
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:1,1,1,1,$TableHeader.bottomSeparatorColor,,10");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        scrollPane.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:3,0,3,0,$Table.background,10,10");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverTrackColor:null");
    }  
    
    private void customizeTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 17));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void setColumnWidths(JTable table, int i, int width) {
        this.setColumnWidths(table, i, width, width, width);
    }
    
    private void setColumnWidths(javax.swing.JTable table, int column, int preferredWidth, int minWidth, int maxWidth) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        tableColumn.setPreferredWidth(preferredWidth);
        tableColumn.setMinWidth(minWidth);
        tableColumn.setMaxWidth(maxWidth);
    }
    
    private void loadItems() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT item_id, item_name FROM Items")) {

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                model.addElement(rs.getString("item_name"));
            }
            cmbItem.setModel(model);
            cmbItem.setSelectedIndex(-1);
        } catch (SQLException | ClassNotFoundException e) {
            showError(e);
        }
    }
    
    private void loadSuppliers() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT supplier_id, supplier_name FROM Suppliers")) {
    
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                model.addElement(rs.getString("supplier_name"));
            }
            cmbSupplier.setModel(model);
            cmbSupplier.setSelectedIndex(-1);
        } catch (SQLException | ClassNotFoundException e) {
            showError(e);
        }
    }
    
    private int getSupplierId(Connection con, String supplierName) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("SELECT supplier_id FROM Suppliers WHERE supplier_name = ?")) {
            pstmt.setString(1, supplierName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("supplier_id");
            } else {
                throw new SQLException("Supplier not found: " + supplierName);
            }
        }
    }
    
    private int getItemId(Connection con, String itemName) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("SELECT item_id FROM Items WHERE item_name = ?")) {
            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("item_id");
            } else {
                throw new SQLException("Item not found: " + itemName);
            }
        }
    }
    
    private void updateItemQuantity(String itemName, int value) {
        int oldValue = 0;
        try (Connection con = getConnection();
             PreparedStatement selectStmt = con.prepareStatement("SELECT quantity_on_hand FROM Items WHERE item_name = ?")) {
            selectStmt.setString(1, itemName);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                oldValue = rs.getInt("quantity_on_hand");
            }
            
            int newValue = oldValue + value;
            
            // Update the new quantity in the database
            try (PreparedStatement updateStmt = con.prepareStatement("UPDATE Items SET quantity_on_hand = ? WHERE item_name = ?")) {
                updateStmt.setInt(1, newValue);
                updateStmt.setString(2, itemName);
                updateStmt.executeUpdate(); // ROLLBACK EXCEPTION
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            showError(e);
        }
    }
    
    private int insertPurchaseOrder(Connection con, String orderDate, int supplierId, double totalAmount) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO PurchaseOrders (order_date, supplier_id, total_amount) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, orderDate);
            pstmt.setInt(2, supplierId);
            pstmt.setDouble(3, totalAmount);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to insert purchase order.");
            }
        }
    }
    
    private void insertPurchaseOrderItem(Connection con, int purchaseOrderId, int itemId, int quantity, double unitPrice) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO PurchaseOrderItems (purchase_order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, purchaseOrderId);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, unitPrice);
            pstmt.executeUpdate();
        }
    }
    
    private void insertTransaction(Connection con, int itemId, String transactionDate, int quantity, String transactionType, String notes) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO Transactions (item_id, transaction_date, quantity, transaction_type, notes) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setInt(1, itemId);
            pstmt.setString(2, transactionDate);
            pstmt.setInt(3, quantity);
            pstmt.setString(4, transactionType);
            pstmt.setString(5, notes);
            pstmt.executeUpdate();
        }
    }
    
    private double getUnitPrice(String itemName) throws SQLException, ClassNotFoundException {
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement("SELECT unit_price FROM Items WHERE item_name = ?")) {
            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("unit_price");
            } else {
                throw new SQLException("Item not found: " + itemName);
            }
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        mainContainer = new javax.swing.JPanel();
        contFT = new javax.swing.JPanel();
        fieldContainer = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notesField = new javax.swing.JTextArea();
        cmbItem = new javax.swing.JComboBox<>();
        quantityField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbSupplier = new javax.swing.JComboBox<>();
        unitPriceField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        addOrderButton = new javax.swing.JButton();
        line = new javax.swing.JPanel();
        tableContainer = new javax.swing.JPanel();
        pane = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        southPanel = new javax.swing.JPanel();
        actionsContainer = new javax.swing.JPanel();
        saveNewButton = new javax.swing.JButton();
        cancelButton1 = new javax.swing.JButton();

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 153));
        jLabel3.setText("Category Name:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1672, 941));
        setPreferredSize(new java.awt.Dimension(1672, 941));
        setResizable(false);

        mainContainer.setLayout(new java.awt.BorderLayout());

        contFT.setLayout(new java.awt.BorderLayout());

        fieldContainer.setBackground(new java.awt.Color(255, 255, 255));
        fieldContainer.setMaximumSize(new java.awt.Dimension(410, 840));
        fieldContainer.setMinimumSize(new java.awt.Dimension(410, 840));
        fieldContainer.setPreferredSize(new java.awt.Dimension(410, 840));
        fieldContainer.setLayout(new java.awt.GridBagLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 153, 153));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("PURCHASE ORDER");
        jLabel4.setMaximumSize(new java.awt.Dimension(410, 48));
        jLabel4.setMinimumSize(new java.awt.Dimension(410, 48));
        jLabel4.setPreferredSize(new java.awt.Dimension(410, 48));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 50, 0);
        fieldContainer.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 153, 153));
        jLabel5.setText("Item:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        fieldContainer.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 153, 153));
        jLabel6.setText("Supplier");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        fieldContainer.add(jLabel6, gridBagConstraints);

        jScrollPane1.setMaximumSize(new java.awt.Dimension(320, 86));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(320, 86));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(320, 86));

        notesField.setColumns(20);
        notesField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        notesField.setLineWrap(true);
        notesField.setRows(5);
        notesField.setTabSize(20);
        notesField.setWrapStyleWord(true);
        jScrollPane1.setViewportView(notesField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        fieldContainer.add(jScrollPane1, gridBagConstraints);

        cmbItem.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbItem.setMaximumSize(new java.awt.Dimension(320, 32));
        cmbItem.setMinimumSize(new java.awt.Dimension(320, 32));
        cmbItem.setPreferredSize(new java.awt.Dimension(320, 32));
        cmbItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbItemActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        fieldContainer.add(cmbItem, gridBagConstraints);

        quantityField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        quantityField.setForeground(new java.awt.Color(51, 51, 51));
        quantityField.setMaximumSize(new java.awt.Dimension(320, 32));
        quantityField.setMinimumSize(new java.awt.Dimension(320, 32));
        quantityField.setPreferredSize(new java.awt.Dimension(320, 32));
        quantityField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        fieldContainer.add(quantityField, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 153, 153));
        jLabel7.setText("Quantity:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        fieldContainer.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 153));
        jLabel8.setText("Notes:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        fieldContainer.add(jLabel8, gridBagConstraints);

        cmbSupplier.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbSupplier.setMaximumSize(new java.awt.Dimension(320, 32));
        cmbSupplier.setMinimumSize(new java.awt.Dimension(320, 32));
        cmbSupplier.setPreferredSize(new java.awt.Dimension(320, 32));
        cmbSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSupplierActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 110, 0);
        fieldContainer.add(cmbSupplier, gridBagConstraints);

        unitPriceField.setEditable(false);
        unitPriceField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        unitPriceField.setForeground(new java.awt.Color(51, 51, 51));
        unitPriceField.setMaximumSize(new java.awt.Dimension(320, 32));
        unitPriceField.setMinimumSize(new java.awt.Dimension(320, 32));
        unitPriceField.setPreferredSize(new java.awt.Dimension(320, 32));
        unitPriceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitPriceFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        fieldContainer.add(unitPriceField, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 153, 153));
        jLabel9.setText("Unit Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        fieldContainer.add(jLabel9, gridBagConstraints);

        addOrderButton.setBackground(new java.awt.Color(0, 153, 153));
        addOrderButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addOrderButton.setForeground(new java.awt.Color(255, 255, 255));
        addOrderButton.setText("Add Order");
        addOrderButton.setBorderPainted(false);
        addOrderButton.setMaximumSize(new java.awt.Dimension(320, 32));
        addOrderButton.setMinimumSize(new java.awt.Dimension(320, 32));
        addOrderButton.setPreferredSize(new java.awt.Dimension(320, 32));
        addOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrderButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 25, 0);
        fieldContainer.add(addOrderButton, gridBagConstraints);

        line.setBackground(new java.awt.Color(0, 153, 153));
        line.setForeground(new java.awt.Color(0, 153, 153));
        line.setMaximumSize(new java.awt.Dimension(320, 3));
        line.setMinimumSize(new java.awt.Dimension(320, 3));
        line.setPreferredSize(new java.awt.Dimension(320, 3));

        javax.swing.GroupLayout lineLayout = new javax.swing.GroupLayout(line);
        line.setLayout(lineLayout);
        lineLayout.setHorizontalGroup(
            lineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        lineLayout.setVerticalGroup(
            lineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(25, 10, 25, 0);
        fieldContainer.add(line, gridBagConstraints);

        contFT.add(fieldContainer, java.awt.BorderLayout.WEST);

        tableContainer.setBackground(new java.awt.Color(255, 255, 255));
        tableContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 5, 5, 40));
        tableContainer.setPreferredSize(new java.awt.Dimension(150, 200));
        tableContainer.setLayout(new java.awt.BorderLayout());

        pane.setPreferredSize(new java.awt.Dimension(150, 200));
        pane.setLayout(new java.awt.BorderLayout());

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        ordersTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        ordersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Header"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ordersTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        ordersTable.setName(""); // NOI18N
        ordersTable.setRowSelectionAllowed(false);
        ordersTable.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(ordersTable);

        pane.add(scroll, java.awt.BorderLayout.CENTER);

        tableContainer.add(pane, java.awt.BorderLayout.CENTER);

        contFT.add(tableContainer, java.awt.BorderLayout.CENTER);

        mainContainer.add(contFT, java.awt.BorderLayout.CENTER);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 35, 35));
        southPanel.setLayout(new java.awt.BorderLayout());

        actionsContainer.setBackground(new java.awt.Color(255, 255, 255));

        saveNewButton.setBackground(new java.awt.Color(0, 153, 153));
        saveNewButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        saveNewButton.setForeground(new java.awt.Color(255, 255, 255));
        saveNewButton.setText("Save New");
        saveNewButton.setBorderPainted(false);
        saveNewButton.setMaximumSize(new java.awt.Dimension(160, 32));
        saveNewButton.setMinimumSize(new java.awt.Dimension(160, 32));
        saveNewButton.setPreferredSize(new java.awt.Dimension(160, 32));
        saveNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewButtonActionPerformed(evt);
            }
        });
        actionsContainer.add(saveNewButton);

        cancelButton1.setBackground(new java.awt.Color(230, 230, 230));
        cancelButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cancelButton1.setForeground(new java.awt.Color(51, 51, 51));
        cancelButton1.setText("Cancel");
        cancelButton1.setBorderPainted(false);
        cancelButton1.setMaximumSize(new java.awt.Dimension(160, 32));
        cancelButton1.setMinimumSize(new java.awt.Dimension(160, 32));
        cancelButton1.setPreferredSize(new java.awt.Dimension(160, 32));
        cancelButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButton1ActionPerformed(evt);
            }
        });
        actionsContainer.add(cancelButton1);

        southPanel.add(actionsContainer, java.awt.BorderLayout.EAST);

        mainContainer.add(southPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainContainer, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButton1ActionPerformed

    private void saveNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewButtonActionPerformed
        String selectedSupplier = (String) cmbSupplier.getSelectedItem();
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (selectedSupplier == null || ordersTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a supplier and add at least one order.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = getConnection()) {

            // Insert into PurchaseOrders table
            int supplierId = getSupplierId(con, selectedSupplier);
            double totalAmount = calculateTotalAmount();
            int purchaseOrderId = insertPurchaseOrder(con, formattedDate, supplierId, totalAmount);

            // Insert into PurchaseOrderItems table and Transactions table
            DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String itemName = (String) model.getValueAt(i, 0);
                int quantity = (int) model.getValueAt(i, 1);
                double unitPrice = (double) model.getValueAt(i, 2);
                String notes = (String) model.getValueAt(i, 4);

                int itemId = getItemId(con, itemName);
                insertPurchaseOrderItem(con, purchaseOrderId, itemId, quantity, unitPrice);
                insertTransaction(con, itemId, formattedDate, quantity, "Stock in", notes);

                // Update item quantity in Items table
                updateItemQuantity(itemName, quantity);
            }

            dispose();
        } catch (SQLException | ClassNotFoundException e) {
            showError(e);
        }
    }//GEN-LAST:event_saveNewButtonActionPerformed

    private void quantityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityFieldActionPerformed

    private void cmbItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbItemActionPerformed
        // TODO add your handling code here:
        String selectedItem = (String) cmbItem.getSelectedItem();
        if (selectedItem != null) {
            try {
                double unitPrice = getUnitPrice(selectedItem);
                unitPriceField.setText(String.valueOf(unitPrice));
            } catch (SQLException | ClassNotFoundException e) {
                showError(e);
            }
        }
    }//GEN-LAST:event_cmbItemActionPerformed

    private void cmbSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSupplierActionPerformed

    private void unitPriceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitPriceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_unitPriceFieldActionPerformed

    private void addOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrderButtonActionPerformed
        // TODO add your handling code here:
        String selectedItem = (String) cmbItem.getSelectedItem();
        String quantityStr = quantityField.getText().trim();
        String unitPriceStr = unitPriceField.getText().trim();
        String notes = notesField.getText().trim();

        if (selectedItem == null || quantityStr.isEmpty() || unitPriceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all the necessary fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double unitPrice = Double.parseDouble(unitPriceStr);
            double amount = quantity * unitPrice;

            DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
            model.addRow(new Object[]{selectedItem, quantity, unitPrice, amount, notes});

            // Clear input fields
            cmbItem.setSelectedIndex(-1);
            quantityField.setText("");
            unitPriceField.setText("");
            notesField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and unit price.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addOrderButtonActionPerformed

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        String DB_URL = SQLConfig.getDbUrl();
        String DB_USER = SQLConfig.getDbUser();
        String DB_PASSWORD = SQLConfig.getDbPassword();
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SavePurchaseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SavePurchaseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SavePurchaseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SavePurchaseDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SavePurchaseDialog dialog = new SavePurchaseDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionsContainer;
    private javax.swing.JButton addOrderButton;
    private javax.swing.JButton cancelButton1;
    private javax.swing.JComboBox<String> cmbItem;
    private javax.swing.JComboBox<String> cmbSupplier;
    private javax.swing.JPanel contFT;
    private javax.swing.JPanel fieldContainer;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel line;
    private javax.swing.JPanel mainContainer;
    private javax.swing.JTextArea notesField;
    private static javax.swing.JTable ordersTable;
    private javax.swing.JPanel pane;
    private javax.swing.JTextField quantityField;
    private javax.swing.JButton saveNewButton;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel tableContainer;
    private javax.swing.JTextField unitPriceField;
    // End of variables declaration//GEN-END:variables
}