/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package final_exam;

import final_exam.utils.SQLConfig;
import final_exam.utils.GradientPanel;
import table.cell.AlternatingRowColorRenderer;
import table.cell.TableActionCellEditor;
import table.cell.TableActionCellRender;
import table.cell.TableActionEvent;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import final_exam.dialogs.category.DeleteCategoryDialog;
import final_exam.dialogs.category.EditCategoryDialog;
import final_exam.dialogs.category.SaveCategoryDialog;
import final_exam.dialogs.category.ViewCategoryDialog;
import final_exam.dialogs.item.DeleteItemDialog;
import final_exam.dialogs.item.EditItemDialog;
import final_exam.dialogs.item.SaveItemDialog;
import final_exam.dialogs.item.ViewItemDialog;
import final_exam.dialogs.supplier.DeleteSupplierDialog;
import final_exam.dialogs.supplier.EditSupplierDialog;
import final_exam.dialogs.supplier.SaveSupplierDialog;
import final_exam.dialogs.supplier.ViewSupplierDialog;
import final_exam.dialogs.transaction.DeleteTransactionDialog;
import final_exam.dialogs.transaction.EditTransactionDialog;
import final_exam.dialogs.transaction.SaveTransactionDialog;
import final_exam.dialogs.transaction.ViewTransactionDialog;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import final_exam.dialogs.purchase.SavePurchaseDialog;
import final_exam.dialogs.purchase.ViewPurchaseDialog;
import javax.swing.DefaultComboBoxModel;
/**
 *
 * @author Kaiyou
 */
public class MainForm extends javax.swing.JFrame {
    SQLConfig sqlConfig = new SQLConfig();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/finals";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    /**
     * Creates new form MainForm
     */
    
    private JButton[] buttons;
    private JButton selectedButton;
    
    public MainForm() {
        sqlConfig.setDbConfig(DB_URL, DB_USER, DB_PASSWORD);
        setLookAndFeel();
        initComponents();
        configureButtons();
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "loginPanel");
//        cardLayout.show(cardPanel, "mainPanel");
        loadCategories();
        loadSuppliers();
        loadCategoriesTable();
        loadSuppliersTable();
        cmbCategories.setSelectedIndex(0);
        loadItemsTable((String) cmbCategories.getSelectedItem());
        loadTransactionsTable();
        cmbSuppliers.setSelectedIndex(0);
        loadPurchasesTable((String) cmbSuppliers.getSelectedItem());
    }    
    
    // ---------------------------------------------------------------------------------------------------------------------------------------
    // CATEGORIES SECTION
    
    private void loadCategoriesTable() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {
            
            applyCustomStyles(categoriesTable, ctPane, ctScroll);
            ResultSet rs = stmt.executeQuery("SELECT * FROM Categories");
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Category Name", "Actions"}, 0);
            categoriesTable.setModel(tableModel);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("category_id"),
                        rs.getString("category_name"),
                        "Actions"
                });
            }

            categoriesTable.getColumn("Actions").setCellRenderer(new TableActionCellRender());
            categoriesTable.getColumn("Actions").setCellEditor(new TableActionCellEditor(new TableActionEvent() {
                @Override
                public void onEdit(int row) {
                    showEditCategoryDialog(row);
                }

                @Override
                public void onDelete(int row) {
                    showDeleteCategoryDialog(row);
                }

                @Override
                public void onView(int row) {
                    showViewCategoryDialog(row);
                }
            }));
            setColumnWidths(categoriesTable, 0, 100);
            setColumnWidths(categoriesTable, 2, 250);
            customizeTableHeader(categoriesTable);
            categoriesTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
            
            categoriesTable.setRowSelectionAllowed(false);
            categoriesTable.setColumnSelectionAllowed(false);
            categoriesTable.setCellSelectionEnabled(false);
            categoriesTable.getTableHeader().setReorderingAllowed(false);
        } catch (Exception e) {
            showError(e);
        }
    }
    
    private void showSaveCategoryDialog() {
        SaveCategoryDialog addDialog = new SaveCategoryDialog(this, true);
        addDialog.setVisible(true);
        loadCategoriesTable();
    }
    
    private void showEditCategoryDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) categoriesTable.getModel();
        int id =  Integer.parseInt((String) model.getValueAt(row, 0));
        String categoryName = (String) model.getValueAt(row, 1);
        
        EditCategoryDialog editDialog = new EditCategoryDialog(this, true, id, categoryName);
        editDialog.setVisible(true);
        loadCategoriesTable();
    }
    
    private void showDeleteCategoryDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) categoriesTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
        
        DeleteCategoryDialog deleteDialog = new DeleteCategoryDialog(this, true, id);
        deleteDialog.setVisible(true);
        loadCategoriesTable();
    }
    
    private void showViewCategoryDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) categoriesTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
    
        ViewCategoryDialog viewDialog = new ViewCategoryDialog(this, true, id);
        viewDialog.setVisible(true);
    }
    
    // ---------------------------------------------------------------------------------------------------------------------------------------
    // SUPPLIERS SECTION
    
    private void loadSuppliersTable() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {
            
            applyCustomStyles(suppliersTable, stPane, stScroll);
            ResultSet rs = stmt.executeQuery("SELECT * FROM Suppliers");
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Supplier Name", "Contact Information", "Actions"}, 0);
            suppliersTable.setModel(tableModel);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("contact_info"),
                        "Actions"
                });
            }

            suppliersTable.getColumn("Actions").setCellRenderer(new TableActionCellRender(true, false, true));
            suppliersTable.getColumn("Actions").setCellEditor(new TableActionCellEditor(new TableActionEvent() {
                @Override
                public void onEdit(int row) {
                    showEditSupplierDialog(row);
                }

                @Override
                public void onDelete(int row) {
                    showDeleteSupplierDialog(row);
                }

                @Override
                public void onView(int row) {
                    showViewSupplierDialog(row);
                }
            }, true, false, true));
            setColumnWidths(suppliersTable, 0, 100);
            setColumnWidths(suppliersTable, 2, 300);
            setColumnWidths(suppliersTable, 3, 250);
            customizeTableHeader(suppliersTable);
            suppliersTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
            
            suppliersTable.setRowSelectionAllowed(false);
            suppliersTable.setColumnSelectionAllowed(false);
            suppliersTable.setCellSelectionEnabled(false);
            suppliersTable.getTableHeader().setReorderingAllowed(false);
        } catch (Exception e) {
            showError(e);
        }
    }
    
    private void showSaveSupplierDialog() {
        SaveSupplierDialog addDialog = new SaveSupplierDialog(this, true);
        addDialog.setVisible(true);
        loadSuppliersTable();
    }
    
    private void showEditSupplierDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) suppliersTable.getModel();
        int id =  Integer.parseInt((String) model.getValueAt(row, 0));
        String name = (String) model.getValueAt(row, 1);
        String contactInfo = (String) model.getValueAt(row, 2);
        
        EditSupplierDialog editDialog = new EditSupplierDialog(this, true, id, name, contactInfo);
        editDialog.setVisible(true);
        loadSuppliersTable();
    }
    
    private void showDeleteSupplierDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) suppliersTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
        
        DeleteSupplierDialog deleteDialog = new DeleteSupplierDialog(this, true, id);
        deleteDialog.setVisible(true);
        loadSuppliersTable();
    }
    
    private void showViewSupplierDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) suppliersTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
    
        ViewSupplierDialog viewDialog = new ViewSupplierDialog(this, true, id);
        viewDialog.setVisible(true);
    }
 
    // ---------------------------------------------------------------------------------------------------------------------------------------
    // ITEMS SECTION
    
    private void loadItemsTable(String categoryFilter) {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {
            
            applyCustomStyles(itemsTable, itPane, itScroll);
            String query = "SELECT i.item_id, i.item_name, i.description, c.category_name, i.unit_price, i.quantity_on_hand, i.reorder_level " +
                       "FROM Items i " +
                       "LEFT JOIN Categories c ON i.category_id = c.category_id";
            
            if (categoryFilter != null && !categoryFilter.equals("All Categories")) {
                query += " WHERE c.category_name = '" + categoryFilter + "'";
            }
            ResultSet rs = stmt.executeQuery(query);
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Item Name", "Description", "Category", "Unit Price", "Stock", "Reorder", "Actions"}, 0);
            itemsTable.setModel(tableModel);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("item_id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getString("category_name"),
                        rs.getDouble("unit_price"),
                        rs.getInt("quantity_on_hand"),
                        rs.getInt("reorder_level"),
                        "Actions"
                });
            }

            itemsTable.getColumn("Actions").setCellRenderer(new TableActionCellRender());
            itemsTable.getColumn("Actions").setCellEditor(new TableActionCellEditor(new TableActionEvent() {
                @Override
                public void onEdit(int row) {
                    showEditItemDialog(row);
                }

                @Override
                public void onDelete(int row) {
                    showDeleteItemDialog(row);
                }

                @Override
                public void onView(int row) {
                    showViewItemDialog(row);
                }
            }));
            setColumnWidths(itemsTable, 0, 75);
            setColumnWidths(itemsTable, 1, 200);
            setColumnWidths(itemsTable, 3, 150);
            setColumnWidths(itemsTable, 4, 100);
            setColumnWidths(itemsTable, 5, 100);
            setColumnWidths(itemsTable, 6, 100);
            setColumnWidths(itemsTable, 7, 250);
            
            customizeTableHeader(itemsTable);
            itemsTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
            
            itemsTable.setRowSelectionAllowed(false);
            itemsTable.setColumnSelectionAllowed(false);
            itemsTable.setCellSelectionEnabled(false);
            itemsTable.getTableHeader().setReorderingAllowed(false);
        } catch (Exception e) {
            showError(e);
        }
    }
    
    private void showSaveItemDialog() {
        SaveItemDialog addDialog = new SaveItemDialog(this, true);
        addDialog.setVisible(true);
        cmbCategories.setSelectedIndex(0);
        loadItemsTable((String) cmbCategories.getSelectedItem());
    }
    
    private void showEditItemDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        int id =  Integer.parseInt((String) model.getValueAt(row, 0));
        EditItemDialog editDialog = new EditItemDialog(this, true, id,
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (String) model.getValueAt(row, 3),
                (double) model.getValueAt(row, 4),
                (int) model.getValueAt(row, 5),
                (int) model.getValueAt(row, 6));
        editDialog.setVisible(true);
        cmbCategories.setSelectedIndex(0);
        loadItemsTable((String) cmbCategories.getSelectedItem());
    }
    
    private void showDeleteItemDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
        
        DeleteItemDialog deleteDialog = new DeleteItemDialog(this, true, id);
        deleteDialog.setVisible(true);
        cmbCategories.setSelectedIndex(0);
        loadItemsTable((String) cmbCategories.getSelectedItem());
    }
    
    private void showViewItemDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
    
        ViewItemDialog viewDialog = new ViewItemDialog(this, true, id);
        viewDialog.setVisible(true);
    }
    
    // ---------------------------------------------------------------------------------------------------------------------------------------
    // TRANSACTIONS SECTION
    
    private void loadTransactionsTable() {
        List<String> gainTypes = Arrays.asList("Stock in", "Purchase", "Restock");
        List<String> lossTypes = Arrays.asList("Stock out", "Sale", "Return", "Damage", "Donation");
        // List<String> neutralTypes = Arrays.asList("Adjustment");
        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {
        
            applyCustomStyles(transactionsTable, ttPane, ttScroll);
            String query = "SELECT t.transaction_id, i.item_name, i.quantity_on_hand, t.transaction_date, t.quantity, t.transaction_type, t.notes " +
                       "FROM Transactions t " +
                       "LEFT JOIN Items i ON t.item_id = i.item_id";
            ResultSet rs = stmt.executeQuery(query);
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Item", "Date", "Quantity", "Type", "Notes", "Actions"}, 0);
//            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Item", "Date", "Quantity", "Type", "Notes"}, 0);
            transactionsTable.setModel(tableModel);
            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                String transactionType = rs.getString("transaction_type");
                String quantityDisplay = "";
                if (lossTypes.contains(transactionType)) {
                    quantityDisplay = "-" + quantity;
                } else if (gainTypes.contains(transactionType)) {
                    quantityDisplay = "+" + quantity;
                } else {
                    quantityDisplay = String.valueOf(quantity);
                }
                
                tableModel.addRow(new Object[]{
                        rs.getString("transaction_id"),
                        rs.getString("item_name"),
                        rs.getString("transaction_date"),
                        quantityDisplay,
                        rs.getString("transaction_type"),
                        rs.getString("notes")
                        ,"Actions"
                });
            }

// DEPRECATED! not
            transactionsTable.getColumn("Actions").setCellRenderer(new TableActionCellRender(false, false, true));
            transactionsTable.getColumn("Actions").setCellEditor(new TableActionCellEditor(new TableActionEvent() {
                @Override
                public void onEdit(int row) {
                    // showEditTransactionDialog(row);
                }

                @Override
                public void onDelete(int row) {
                    // showDeleteTransactionDialog(row);
                }

                @Override
                public void onView(int row) {
                    showViewTransactionDialog(row);
                }
            }, false, false, true));

            setColumnWidths(transactionsTable, 0, 100);
            setColumnWidths(transactionsTable, 1, 200);
            setColumnWidths(transactionsTable, 2, 225);
            setColumnWidths(transactionsTable, 3, 120);
            setColumnWidths(transactionsTable, 4, 120);
            setColumnWidths(transactionsTable, 6, 250); // 93
            customizeTableHeader(transactionsTable);
            transactionsTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
            
            transactionsTable.setRowSelectionAllowed(false);
            transactionsTable.setColumnSelectionAllowed(false);
            transactionsTable.setCellSelectionEnabled(false);
            transactionsTable.getTableHeader().setReorderingAllowed(false);
        } catch (Exception e) {
            showError(e);
        }
    }
    
    private void showSaveTransactionDialog() {
        SaveTransactionDialog addDialog = new SaveTransactionDialog(this, true);
        addDialog.setVisible(true);
        loadTransactionsTable();
    }
    
    private void showEditTransactionDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
        String itemName = (String) model.getValueAt(row, 1);
        int quantity = Math.abs(Integer.parseInt(model.getValueAt(row, 3).toString()));
        String transactionType = (String) model.getValueAt(row, 4);
        String notes = (String) model.getValueAt(row, 5);

        EditTransactionDialog editDialog = new EditTransactionDialog(this, true, id, itemName, transactionType, quantity, notes);
        editDialog.setVisible(true);
        loadTransactionsTable();
    }
    
    private void showDeleteTransactionDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        int id = Integer.parseInt((String) model.getValueAt(row, 0));
        
        DeleteTransactionDialog deleteDialog = new DeleteTransactionDialog(this, true, id);
        deleteDialog.setVisible(true);
    }
    
    private void showViewTransactionDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        ViewTransactionDialog viewDialog = new ViewTransactionDialog(this, true, id);
        viewDialog.setVisible(true);
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------
    // PURCHASES SECTION
    private void loadPurchasesTable(String supplierFilter) {
        try (Connection con = getConnection();
            Statement stmt = con.createStatement()) {
            
            applyCustomStyles(purchasesTable, ptPane, ptScroll);
            String query = "SELECT po.purchase_order_id, po.order_date, s.supplier_name, po.total_amount, COUNT(poi.item_id) AS number_of_items " +
                       "FROM PurchaseOrders po " +
                       "JOIN Suppliers s ON po.supplier_id = s.supplier_id " +
                       "JOIN PurchaseOrderItems poi ON po.purchase_order_id = poi.purchase_order_id";
        
            if (supplierFilter != null && !supplierFilter.equals("All Suppliers")) {
                query += " WHERE s.supplier_name = '" + supplierFilter + "'";
            }
            
            query += " GROUP BY po.purchase_order_id, po.order_date, s.supplier_name, po.total_amount";
            
            ResultSet rs = stmt.executeQuery(query);
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Supplier", "Number of Items", "Total Amount", "Actions"}, 0);
            purchasesTable.setModel(tableModel);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("purchase_order_id"),
                        rs.getString("order_date"),
                        rs.getString("supplier_name"),
                        rs.getInt("number_of_items"),
                        rs.getDouble("total_amount"),
                        "Actions"
                });
            }
            
            purchasesTable.getColumn("Actions").setCellRenderer(new TableActionCellRender(false, false, true));
            purchasesTable.getColumn("Actions").setCellEditor(new TableActionCellEditor(new TableActionEvent() {
                @Override
                public void onEdit(int row) {
                }

                @Override
                public void onDelete(int row) {
                }

                @Override
                public void onView(int row) {
                    showViewPurchaseDialog(row);
                }
            }, false, false, true));


            setColumnWidths(purchasesTable, 0, 100);
            setColumnWidths(purchasesTable, 1, 225);
            // setColumnWidths(purchasesTable, 2, 200);
            setColumnWidths(purchasesTable, 3, 200);
            setColumnWidths(purchasesTable, 4, 150);
            setColumnWidths(purchasesTable, 5, 250);
            customizeTableHeader(purchasesTable);
            purchasesTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
            
            purchasesTable.setRowSelectionAllowed(false);
            purchasesTable.setColumnSelectionAllowed(false);
            purchasesTable.setCellSelectionEnabled(false);
            purchasesTable.getTableHeader().setReorderingAllowed(false);
        } catch (Exception e) {
            showError(e);
        }
    }
    
    private void showSavePurchaseDialog() {
        SavePurchaseDialog addDialog = new SavePurchaseDialog(this, true);
        addDialog.setVisible(true);
        cmbSuppliers.setSelectedIndex(0);
        loadPurchasesTable((String) cmbSuppliers.getSelectedItem());
    }
    
    private void showViewPurchaseDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) purchasesTable.getModel();
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        ViewPurchaseDialog viewDialog = new ViewPurchaseDialog(this, true, id);
        viewDialog.setVisible(true);
    }
    
    // ---------------------------------------------------------------------------------------------------------------------------------------
    
    private void configureButtons() {
        buttons = new JButton[]{btnCategories, btnSuppliers, btnItems, btnTransactions, btnPurchases};

        for (JButton button : buttons) {
            button.setBackground(new Color(0, 0, 0, 0));
            button.setForeground(Color.WHITE);
            addHoverEffect(button);
        }

        // Add click effect to all buttons
        addClickEffect(buttons);
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

    
    private void addHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Color originalFGColor = button.getForeground();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(new Color(0, 0, 0, 75));
                    button.setForeground(new Color(255,204,0));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(new Color(0,0,0,0));
                    button.setForeground(originalFGColor);
                }
            }
        });
    }
    
    private void addClickEffect(JButton[] buttons) {
        for (JButton button : buttons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JButton btn : buttons) {
                        btn.setBackground(new Color(0,0,0,0));
                        btn.setForeground(Color.white);
                    }
                    button.setBackground(Color.white);
                    button.setForeground(Color.red);
                    selectedButton = button;
                }
            });
        }
    }
    
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder sb = new StringBuilder();
        boolean newWord = true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (newWord && Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
                newWord = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }

            if (c == ' ') {
                newWord = true;
            }
        }
        
        return sb.toString();
    }
    
    // Set look and feel to metal
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
    
    // Get a connection to the database
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Clear the input fields
    private void clearFields() {
        lpEmailField.setText("");
        lpPasswordField.setText("");
        rpFirstNameField.setText("");
        rpLastNameField.setText("");
        rpEmailField.setText("");
        rpPasswordField.setText("");
    }

    // Show an error message
    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        cardPanel = new javax.swing.JPanel();
        loginPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lpEmailField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lpLogin = new javax.swing.JButton();
        lpRegister = new javax.swing.JButton();
        lpPasswordField = new javax.swing.JPasswordField();
        registerPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rpFirstNameField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        rpLastNameField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        rpEmailField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        rpRegister = new javax.swing.JButton();
        rpLogin = new javax.swing.JButton();
        rpPasswordField = new javax.swing.JPasswordField();
        mainPanel = new javax.swing.JPanel();
        mainMenu = new javax.swing.JPanel();
        btnCategories = new javax.swing.JButton();
        btnSuppliers = new javax.swing.JButton();
        btnItems = new javax.swing.JButton();
        btnTransactions = new javax.swing.JButton();
        btnLogOut = new javax.swing.JButton();
        btnPurchases = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        nameDisplay = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        mainScreen = new javax.swing.JPanel();
        categoriesPanel = new javax.swing.JPanel();
        ctContainer = new javax.swing.JPanel();
        ctNorth = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        buttonContainer = new javax.swing.JPanel();
        categoriesAddButton = new javax.swing.JButton();
        ctPane = new javax.swing.JPanel();
        ctScroll = new javax.swing.JScrollPane();
        categoriesTable = new javax.swing.JTable();
        suppliersPanel = new javax.swing.JPanel();
        stContainer = new javax.swing.JPanel();
        stNorth = new javax.swing.JPanel();
        title1 = new javax.swing.JLabel();
        buttonContainer1 = new javax.swing.JPanel();
        suppliersAddButton = new javax.swing.JButton();
        stPane = new javax.swing.JPanel();
        stScroll = new javax.swing.JScrollPane();
        suppliersTable = new javax.swing.JTable();
        itemsPanel = new javax.swing.JPanel();
        itContainer = new javax.swing.JPanel();
        itNorth = new javax.swing.JPanel();
        title2 = new javax.swing.JLabel();
        buttonContainer2 = new javax.swing.JPanel();
        itemsAddButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        cmbCategories = new javax.swing.JComboBox<>();
        showAllItemsButton = new javax.swing.JButton();
        itPane = new javax.swing.JPanel();
        itScroll = new javax.swing.JScrollPane();
        itemsTable = new javax.swing.JTable();
        transactionsPanel = new javax.swing.JPanel();
        ttContainer = new javax.swing.JPanel();
        ttNorth = new javax.swing.JPanel();
        title3 = new javax.swing.JLabel();
        buttonContainer3 = new javax.swing.JPanel();
        ttPane = new javax.swing.JPanel();
        ttScroll = new javax.swing.JScrollPane();
        transactionsTable = new javax.swing.JTable();
        purchasesPanel = new javax.swing.JPanel();
        ptContainer = new javax.swing.JPanel();
        ptNorth = new javax.swing.JPanel();
        title4 = new javax.swing.JLabel();
        buttonContainer4 = new javax.swing.JPanel();
        purchasesAddButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cmbSuppliers = new javax.swing.JComboBox<>();
        showAllPurchasesButton = new javax.swing.JButton();
        ptPane = new javax.swing.JPanel();
        ptScroll = new javax.swing.JScrollPane();
        purchasesTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IMS-DB - [Kaiyou Serra]");
        setMinimumSize(new java.awt.Dimension(1760, 990));

        cardPanel.setBackground(new java.awt.Color(242, 242, 242));
        cardPanel.setLayout(new java.awt.CardLayout());

        //loginPanel = new GradientPanel(new Color(104, 14, 81), new Color(53, 0, 85));
        loginPanel.setBackground(new java.awt.Color(0, 0, 102));
        loginPanel.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setText("Login");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 14;
        jPanel2.add(jLabel11, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.insets = new java.awt.Insets(35, 0, 0, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

        lpEmailField.setBackground(new java.awt.Color(255, 255, 255));
        lpEmailField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lpEmailField.setForeground(new java.awt.Color(51, 51, 51));
        lpEmailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpEmailFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(lpEmailField, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        jPanel2.add(jLabel9, gridBagConstraints);

        lpLogin.setBackground(new java.awt.Color(55, 186, 48));
        lpLogin.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lpLogin.setForeground(new java.awt.Color(255, 255, 255));
        lpLogin.setText("Log in");
        lpLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lpLogin.setFocusPainted(false);
        lpLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpLoginActionPerformed(evt);
            }
        });
        lpLogin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lpLoginKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        jPanel2.add(lpLogin, gridBagConstraints);

        lpRegister.setBackground(new Color(0,0,0,0));
        lpRegister.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lpRegister.setForeground(new java.awt.Color(0, 153, 204));
        lpRegister.setText("No Account? Register Now!");
        lpRegister.setBorderPainted(false);
        lpRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lpRegister.setFocusPainted(false);
        lpRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lpRegisterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(lpRegister, gridBagConstraints);

        lpPasswordField.setBackground(new java.awt.Color(255, 255, 255));
        lpPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lpPasswordField.setNextFocusableComponent(lpLogin);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(lpPasswordField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.ipady = 60;
        loginPanel.add(jPanel2, gridBagConstraints);

        cardPanel.add(loginPanel, "loginPanel");

        //registerPanel = new GradientPanel(new Color(0, 0, 102), new Color(200, 0, 52));
        registerPanel.setBackground(new java.awt.Color(0, 0, 102));
        registerPanel.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Registration");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel3.add(jLabel12, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("First Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(35, 0, 0, 0);
        jPanel3.add(jLabel4, gridBagConstraints);

        rpFirstNameField.setBackground(new java.awt.Color(255, 255, 255));
        rpFirstNameField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rpFirstNameField.setForeground(new java.awt.Color(51, 51, 51));
        rpFirstNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rpFirstNameFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(rpFirstNameField, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Last Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        jPanel3.add(jLabel10, gridBagConstraints);

        rpLastNameField.setBackground(new java.awt.Color(255, 255, 255));
        rpLastNameField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rpLastNameField.setForeground(new java.awt.Color(51, 51, 51));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(rpLastNameField, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Email:");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        jPanel3.add(jLabel5, gridBagConstraints);

        rpEmailField.setBackground(new java.awt.Color(255, 255, 255));
        rpEmailField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rpEmailField.setForeground(new java.awt.Color(51, 51, 51));
        rpEmailField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rpEmailFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(rpEmailField, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Password:");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        jPanel3.add(jLabel6, gridBagConstraints);

        rpRegister.setBackground(new java.awt.Color(46, 169, 250));
        rpRegister.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        rpRegister.setForeground(new java.awt.Color(255, 255, 255));
        rpRegister.setText("Register");
        rpRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rpRegister.setFocusPainted(false);
        rpRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rpRegisterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 0, 0);
        jPanel3.add(rpRegister, gridBagConstraints);

        rpLogin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rpLogin.setForeground(new java.awt.Color(0, 153, 204));
        rpLogin.setText("Go Back to Login Page!");
        rpLogin.setBorderPainted(false);
        rpLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rpLogin.setFocusPainted(false);
        rpLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rpLoginActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel3.add(rpLogin, gridBagConstraints);

        rpPasswordField.setBackground(new java.awt.Color(255, 255, 255));
        rpPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(rpPasswordField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.ipady = 60;
        registerPanel.add(jPanel3, gridBagConstraints);

        cardPanel.add(registerPanel, "registerPanel");

        mainPanel.setBackground(new java.awt.Color(0, 0, 102));
        mainPanel.setPreferredSize(new java.awt.Dimension(1760, 990));
        mainPanel.setLayout(new java.awt.BorderLayout());

        mainMenu = new GradientPanel(new Color(0, 0, 102), new Color(200, 0, 52));
        mainMenu.setBackground(new java.awt.Color(0, 0, 102));
        mainMenu.setLayout(new java.awt.GridBagLayout());

        btnCategories.setBackground(new Color(0, 0, 0, 0));
        btnCategories.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCategories.setForeground(new java.awt.Color(255, 255, 255));
        btnCategories.setText("CATEGORIES");
        btnCategories.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 55, 1, 1));
        btnCategories.setFocusPainted(false);
        btnCategories.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.ipady = 30;
        mainMenu.add(btnCategories, gridBagConstraints);

        btnSuppliers.setBackground(new Color(0, 0, 0, 0));
        btnSuppliers.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSuppliers.setForeground(new java.awt.Color(255, 255, 255));
        btnSuppliers.setText("SUPPLIERS");
        btnSuppliers.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 55, 1, 1));
        btnSuppliers.setFocusPainted(false);
        btnSuppliers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuppliersActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.ipady = 30;
        mainMenu.add(btnSuppliers, gridBagConstraints);

        btnItems.setBackground(new Color(0, 0, 0, 0));
        btnItems.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnItems.setForeground(new java.awt.Color(255, 255, 255));
        btnItems.setText("ITEMS");
        btnItems.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 55, 1, 1));
        btnItems.setFocusPainted(false);
        btnItems.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnItemsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.ipady = 30;
        mainMenu.add(btnItems, gridBagConstraints);

        btnTransactions.setBackground(new Color(0, 0, 0, 0));
        btnTransactions.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnTransactions.setForeground(new java.awt.Color(255, 255, 255));
        btnTransactions.setText("TRANSACTIONS");
        btnTransactions.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 55, 1, 1));
        btnTransactions.setFocusPainted(false);
        btnTransactions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTransactions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.ipady = 30;
        mainMenu.add(btnTransactions, gridBagConstraints);

        btnLogOut.setBackground(new java.awt.Color(255, 255, 255));
        btnLogOut.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogOut.setForeground(new java.awt.Color(0, 0, 204));
        btnLogOut.setText("Log Out");
        btnLogOut.setFocusPainted(false);
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(17, 37, 100, 37);
        mainMenu.add(btnLogOut, gridBagConstraints);

        btnPurchases.setBackground(new Color(0, 0, 0, 0));
        btnPurchases.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPurchases.setForeground(new java.awt.Color(255, 255, 255));
        btnPurchases.setText("PURCHASE ORDERS");
        btnPurchases.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 55, 1, 1));
        btnPurchases.setFocusPainted(false);
        btnPurchases.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPurchases.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPurchasesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.ipady = 30;
        mainMenu.add(btnPurchases, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome,");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.insets = new java.awt.Insets(250, 37, 7, 37);
        mainMenu.add(jLabel1, gridBagConstraints);

        nameDisplay.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        nameDisplay.setForeground(new java.awt.Color(255, 255, 255));
        nameDisplay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameDisplay.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.insets = new java.awt.Insets(7, 37, 17, 37);
        mainMenu.add(nameDisplay, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("INVENTORY");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.insets = new java.awt.Insets(50, 25, 2, 25);
        mainMenu.add(jLabel3, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("MANAGEMENT");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 2, 25);
        mainMenu.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("SYSTEM DATABASE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 50, 25);
        mainMenu.add(jLabel8, gridBagConstraints);

        mainPanel.add(mainMenu, java.awt.BorderLayout.WEST);

        mainScreen.setBackground(new java.awt.Color(255, 255, 255));
        mainScreen.setName("mainScreen"); // NOI18N
        mainScreen.setLayout(new java.awt.CardLayout());

        categoriesPanel.setBackground(new java.awt.Color(242, 242, 242));
        categoriesPanel.setPreferredSize(new java.awt.Dimension(1475, 990));
        categoriesPanel.setLayout(new java.awt.BorderLayout());

        ctContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));
        ctContainer.setLayout(new java.awt.BorderLayout());

        ctNorth.setBackground(new Color(0, 0, 0, 0));
        ctNorth.setLayout(new java.awt.BorderLayout());

        title.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        title.setForeground(new java.awt.Color(51, 51, 51));
        title.setText("CATEGORIES");
        title.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 1, 0));
        title.setVerifyInputWhenFocusTarget(false);
        ctNorth.add(title, java.awt.BorderLayout.NORTH);

        buttonContainer.setBackground(new Color(0, 0, 0, 0));
        buttonContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        categoriesAddButton.setBackground(new java.awt.Color(72, 176, 44));
        categoriesAddButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        categoriesAddButton.setForeground(new java.awt.Color(255, 255, 255));
        categoriesAddButton.setText("Add New");
        categoriesAddButton.setBorderPainted(false);
        categoriesAddButton.setMargin(new java.awt.Insets(2, 25, 3, 25));
        categoriesAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoriesAddButtonActionPerformed(evt);
            }
        });
        buttonContainer.add(categoriesAddButton);

        ctNorth.add(buttonContainer, java.awt.BorderLayout.SOUTH);

        ctContainer.add(ctNorth, java.awt.BorderLayout.NORTH);

        ctPane.setLayout(new java.awt.BorderLayout());

        ctScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 32, 32, 32));

        categoriesTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        categoriesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Category Name", "Actions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        categoriesTable.setName(""); // NOI18N
        categoriesTable.setRowSelectionAllowed(false);
        categoriesTable.getTableHeader().setReorderingAllowed(false);
        ctScroll.setViewportView(categoriesTable);
        categoriesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (categoriesTable.getColumnModel().getColumnCount() > 0) {
            categoriesTable.getColumnModel().getColumn(0).setMinWidth(100);
            categoriesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            categoriesTable.getColumnModel().getColumn(0).setMaxWidth(100);
            categoriesTable.getColumnModel().getColumn(0).setHeaderValue("ID");
            categoriesTable.getColumnModel().getColumn(1).setHeaderValue("Category Name");
            categoriesTable.getColumnModel().getColumn(2).setMinWidth(400);
            categoriesTable.getColumnModel().getColumn(2).setPreferredWidth(400);
            categoriesTable.getColumnModel().getColumn(2).setMaxWidth(400);
        }

        ctPane.add(ctScroll, java.awt.BorderLayout.CENTER);

        ctContainer.add(ctPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(ctContainer, java.awt.BorderLayout.CENTER);

        mainScreen.add(categoriesPanel, "categoriesPanel");

        suppliersPanel.setBackground(new java.awt.Color(255, 255, 204));
        suppliersPanel.setLayout(new java.awt.BorderLayout());

        stContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));
        stContainer.setLayout(new java.awt.BorderLayout());

        stNorth.setBackground(new Color(0, 0, 0, 0));
        stNorth.setLayout(new java.awt.BorderLayout());

        title1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        title1.setForeground(new java.awt.Color(51, 51, 51));
        title1.setText("SUPPLIERS");
        title1.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 1, 0));
        title1.setVerifyInputWhenFocusTarget(false);
        stNorth.add(title1, java.awt.BorderLayout.NORTH);

        buttonContainer1.setBackground(new Color(0, 0, 0, 0));
        buttonContainer1.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonContainer1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        suppliersAddButton.setBackground(new java.awt.Color(72, 176, 44));
        suppliersAddButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        suppliersAddButton.setForeground(new java.awt.Color(255, 255, 255));
        suppliersAddButton.setText("Add New");
        suppliersAddButton.setBorderPainted(false);
        suppliersAddButton.setMargin(new java.awt.Insets(2, 25, 3, 25));
        suppliersAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suppliersAddButtonActionPerformed(evt);
            }
        });
        buttonContainer1.add(suppliersAddButton);

        stNorth.add(buttonContainer1, java.awt.BorderLayout.SOUTH);

        stContainer.add(stNorth, java.awt.BorderLayout.NORTH);

        stPane.setLayout(new java.awt.BorderLayout());

        stScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 32, 32, 32));

        suppliersTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        suppliersTable.setModel(new javax.swing.table.DefaultTableModel(
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
        suppliersTable.setColumnSelectionAllowed(true);
        suppliersTable.setName(""); // NOI18N
        suppliersTable.setRowSelectionAllowed(false);
        suppliersTable.getTableHeader().setReorderingAllowed(false);
        stScroll.setViewportView(suppliersTable);
        suppliersTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (suppliersTable.getColumnModel().getColumnCount() > 0) {
            suppliersTable.getColumnModel().getColumn(0).setMinWidth(400);
            suppliersTable.getColumnModel().getColumn(0).setPreferredWidth(400);
            suppliersTable.getColumnModel().getColumn(0).setMaxWidth(400);
        }

        stPane.add(stScroll, java.awt.BorderLayout.CENTER);

        stContainer.add(stPane, java.awt.BorderLayout.CENTER);

        suppliersPanel.add(stContainer, java.awt.BorderLayout.CENTER);

        mainScreen.add(suppliersPanel, "suppliersPanel");

        itemsPanel.setBackground(new java.awt.Color(204, 255, 204));
        itemsPanel.setLayout(new java.awt.BorderLayout());

        itContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));
        itContainer.setLayout(new java.awt.BorderLayout());

        itNorth.setBackground(new Color(0, 0, 0, 0));
        itNorth.setLayout(new java.awt.BorderLayout());

        title2.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        title2.setForeground(new java.awt.Color(51, 51, 51));
        title2.setText("ITEMS");
        title2.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 1, 0));
        title2.setVerifyInputWhenFocusTarget(false);
        itNorth.add(title2, java.awt.BorderLayout.NORTH);

        buttonContainer2.setBackground(new Color(0, 0, 0, 0));
        buttonContainer2.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonContainer2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        itemsAddButton.setBackground(new java.awt.Color(72, 176, 44));
        itemsAddButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        itemsAddButton.setForeground(new java.awt.Color(255, 255, 255));
        itemsAddButton.setText("Add New");
        itemsAddButton.setBorderPainted(false);
        itemsAddButton.setMargin(new java.awt.Insets(2, 25, 3, 25));
        itemsAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemsAddButtonActionPerformed(evt);
            }
        });
        buttonContainer2.add(itemsAddButton);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(158, 19, 158));
        jLabel14.setText("       Filter:");
        buttonContainer2.add(jLabel14);

        cmbCategories.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCategories.setMaximumSize(new java.awt.Dimension(320, 32));
        cmbCategories.setMinimumSize(new java.awt.Dimension(320, 32));
        cmbCategories.setPreferredSize(new java.awt.Dimension(320, 32));
        cmbCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoriesActionPerformed(evt);
            }
        });
        buttonContainer2.add(cmbCategories);

        showAllItemsButton.setBackground(new java.awt.Color(158, 19, 158));
        showAllItemsButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        showAllItemsButton.setForeground(new java.awt.Color(255, 255, 255));
        showAllItemsButton.setText("Show All");
        showAllItemsButton.setBorderPainted(false);
        showAllItemsButton.setMargin(new java.awt.Insets(2, 25, 3, 25));
        showAllItemsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllItemsButtonActionPerformed(evt);
            }
        });
        buttonContainer2.add(showAllItemsButton);

        itNorth.add(buttonContainer2, java.awt.BorderLayout.SOUTH);

        itContainer.add(itNorth, java.awt.BorderLayout.NORTH);

        itPane.setLayout(new java.awt.BorderLayout());

        itScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 32, 32, 32));

        itemsTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        itemsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        itemsTable.setName(""); // NOI18N
        itemsTable.setRowSelectionAllowed(false);
        itemsTable.getTableHeader().setReorderingAllowed(false);
        itScroll.setViewportView(itemsTable);
        itemsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (itemsTable.getColumnModel().getColumnCount() > 0) {
            itemsTable.getColumnModel().getColumn(0).setMinWidth(400);
            itemsTable.getColumnModel().getColumn(0).setPreferredWidth(400);
            itemsTable.getColumnModel().getColumn(0).setMaxWidth(400);
        }

        itPane.add(itScroll, java.awt.BorderLayout.CENTER);

        itContainer.add(itPane, java.awt.BorderLayout.CENTER);

        itemsPanel.add(itContainer, java.awt.BorderLayout.CENTER);

        mainScreen.add(itemsPanel, "itemsPanel");

        transactionsPanel.setBackground(new java.awt.Color(204, 255, 255));
        transactionsPanel.setLayout(new java.awt.BorderLayout());

        ttContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));
        ttContainer.setLayout(new java.awt.BorderLayout());

        ttNorth.setBackground(new Color(0, 0, 0, 0));
        ttNorth.setLayout(new java.awt.BorderLayout());

        title3.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        title3.setForeground(new java.awt.Color(51, 51, 51));
        title3.setText("TRANSACTIONS");
        title3.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 1, 0));
        title3.setRequestFocusEnabled(false);
        title3.setVerifyInputWhenFocusTarget(false);
        ttNorth.add(title3, java.awt.BorderLayout.NORTH);

        buttonContainer3.setBackground(new Color(0, 0, 0, 0));
        buttonContainer3.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonContainer3.setPreferredSize(new java.awt.Dimension(140, 67));
        buttonContainer3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        ttNorth.add(buttonContainer3, java.awt.BorderLayout.SOUTH);

        ttContainer.add(ttNorth, java.awt.BorderLayout.NORTH);

        ttPane.setLayout(new java.awt.BorderLayout());

        ttScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 32, 32, 32));

        transactionsTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        transactionsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        transactionsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        transactionsTable.setName(""); // NOI18N
        transactionsTable.setRowSelectionAllowed(false);
        transactionsTable.getTableHeader().setReorderingAllowed(false);
        ttScroll.setViewportView(transactionsTable);
        transactionsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (transactionsTable.getColumnModel().getColumnCount() > 0) {
            transactionsTable.getColumnModel().getColumn(0).setMinWidth(400);
            transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(400);
            transactionsTable.getColumnModel().getColumn(0).setMaxWidth(400);
        }

        ttPane.add(ttScroll, java.awt.BorderLayout.CENTER);

        ttContainer.add(ttPane, java.awt.BorderLayout.CENTER);

        transactionsPanel.add(ttContainer, java.awt.BorderLayout.CENTER);

        mainScreen.add(transactionsPanel, "transactionsPanel");

        purchasesPanel.setBackground(new java.awt.Color(204, 255, 255));
        purchasesPanel.setLayout(new java.awt.BorderLayout());

        ptContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));
        ptContainer.setLayout(new java.awt.BorderLayout());

        ptNorth.setBackground(new Color(0, 0, 0, 0));
        ptNorth.setLayout(new java.awt.BorderLayout());

        title4.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        title4.setForeground(new java.awt.Color(51, 51, 51));
        title4.setText("PURCHASE ORDERS");
        title4.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 1, 0));
        title4.setRequestFocusEnabled(false);
        title4.setVerifyInputWhenFocusTarget(false);
        ptNorth.add(title4, java.awt.BorderLayout.NORTH);

        buttonContainer4.setBackground(new Color(0, 0, 0, 0));
        buttonContainer4.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 0, 0, 0));
        buttonContainer4.setPreferredSize(new java.awt.Dimension(140, 67));
        buttonContainer4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        purchasesAddButton.setBackground(new java.awt.Color(72, 176, 44));
        purchasesAddButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        purchasesAddButton.setForeground(new java.awt.Color(255, 255, 255));
        purchasesAddButton.setText("Add New");
        purchasesAddButton.setBorderPainted(false);
        purchasesAddButton.setMargin(new java.awt.Insets(2, 25, 3, 25));
        purchasesAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchasesAddButtonActionPerformed(evt);
            }
        });
        buttonContainer4.add(purchasesAddButton);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(158, 19, 158));
        jLabel15.setText("       Filter:");
        buttonContainer4.add(jLabel15);

        cmbSuppliers.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbSuppliers.setMaximumSize(new java.awt.Dimension(320, 32));
        cmbSuppliers.setMinimumSize(new java.awt.Dimension(320, 32));
        cmbSuppliers.setPreferredSize(new java.awt.Dimension(320, 32));
        cmbSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSuppliersActionPerformed(evt);
            }
        });
        buttonContainer4.add(cmbSuppliers);

        showAllPurchasesButton.setBackground(new java.awt.Color(158, 19, 158));
        showAllPurchasesButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        showAllPurchasesButton.setForeground(new java.awt.Color(255, 255, 255));
        showAllPurchasesButton.setText("Show All");
        showAllPurchasesButton.setBorderPainted(false);
        showAllPurchasesButton.setMargin(new java.awt.Insets(2, 25, 3, 25));
        showAllPurchasesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllPurchasesButtonActionPerformed(evt);
            }
        });
        buttonContainer4.add(showAllPurchasesButton);

        ptNorth.add(buttonContainer4, java.awt.BorderLayout.SOUTH);

        ptContainer.add(ptNorth, java.awt.BorderLayout.NORTH);

        ptPane.setLayout(new java.awt.BorderLayout());

        ptScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 32, 32, 32));

        purchasesTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        purchasesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        purchasesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        purchasesTable.setName(""); // NOI18N
        purchasesTable.setRowSelectionAllowed(false);
        purchasesTable.getTableHeader().setReorderingAllowed(false);
        ptScroll.setViewportView(purchasesTable);
        purchasesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (purchasesTable.getColumnModel().getColumnCount() > 0) {
            purchasesTable.getColumnModel().getColumn(0).setMinWidth(400);
            purchasesTable.getColumnModel().getColumn(0).setPreferredWidth(400);
            purchasesTable.getColumnModel().getColumn(0).setMaxWidth(400);
        }

        ptPane.add(ptScroll, java.awt.BorderLayout.CENTER);

        ptContainer.add(ptPane, java.awt.BorderLayout.CENTER);

        purchasesPanel.add(ptContainer, java.awt.BorderLayout.CENTER);

        mainScreen.add(purchasesPanel, "purchasesPanel");

        mainPanel.add(mainScreen, java.awt.BorderLayout.CENTER);

        cardPanel.add(mainPanel, "mainPanel");

        getContentPane().add(cardPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lpEmailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpEmailFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lpEmailFieldActionPerformed

    private void lpLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpLoginActionPerformed
        // TODO add your handling code here:
        String email = lpEmailField.getText().toLowerCase().trim();
        String password = lpPasswordField.getText();
        boolean isAuthenticated = false;

        if (email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill out all the necessary fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT firstname, lastname FROM users WHERE email = ? AND password = ?;";
        try (Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            isAuthenticated = rs.next();

            if (isAuthenticated) {
                CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
                nameDisplay.setText(rs.getString("firstname") + " " + rs.getString("lastname"));
                cardLayout.show(cardPanel, "mainPanel");
                btnCategories.setBackground(Color.white);
                btnCategories.setForeground(Color.red);
                selectedButton = btnCategories;
                System.out.println("LOGGED IN");
            } else {
                JOptionPane.showMessageDialog(this, "No user found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            clearFields();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_lpLoginActionPerformed

    private void lpRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lpRegisterActionPerformed
        // TODO add your handling code here:
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "registerPanel");
    }//GEN-LAST:event_lpRegisterActionPerformed

    private void rpLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rpLoginActionPerformed
        // TODO add your handling code here:
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "loginPanel");
    }//GEN-LAST:event_rpLoginActionPerformed

    private void rpRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rpRegisterActionPerformed
        // TODO add your handling code here:
        String firstName = toTitleCase(rpFirstNameField.getText().trim());
        String lastName = toTitleCase(rpLastNameField.getText().trim());
        String email = rpEmailField.getText().toLowerCase().trim();
        String password = rpPasswordField.getText();
        
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill out all the necessary fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO users (lastname, firstname, email, password) VALUES (?, ?, ?, ?);";
        try (Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.executeUpdate();

            clearFields();
            JOptionPane.showMessageDialog(this, "Account created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_rpRegisterActionPerformed
    
    private void loadCategories() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT category_name FROM Categories")) {

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("All Categories");
            while (rs.next()) {
                model.addElement(rs.getString("category_name"));
            }
            cmbCategories.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSuppliers() {
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT supplier_name FROM Suppliers")) {

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("All Suppliers");
            while (rs.next()) {
                model.addElement(rs.getString("supplier_name"));
            }
            cmbSuppliers.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void rpEmailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rpEmailFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rpEmailFieldActionPerformed

    private void rpFirstNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rpFirstNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rpFirstNameFieldActionPerformed

    private void btnCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriesActionPerformed
        // TODO add your handling code here:
        loadCategoriesTable();
        CardLayout cardLayout = (CardLayout) mainScreen.getLayout();
        cardLayout.show(mainScreen, "categoriesPanel");
    }//GEN-LAST:event_btnCategoriesActionPerformed

    private void btnSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuppliersActionPerformed
        // TODO add your handling code here:
        loadSuppliersTable();
        CardLayout cardLayout = (CardLayout) mainScreen.getLayout();
        cardLayout.show(mainScreen, "suppliersPanel");
    }//GEN-LAST:event_btnSuppliersActionPerformed

    private void btnItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnItemsActionPerformed
        // TODO add your handling code here:
        cmbCategories.setSelectedIndex(0);
        loadItemsTable((String) cmbCategories.getSelectedItem());
        CardLayout cardLayout = (CardLayout) mainScreen.getLayout();
        cardLayout.show(mainScreen, "itemsPanel");
    }//GEN-LAST:event_btnItemsActionPerformed

    private void btnTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionsActionPerformed
        // TODO add your handling code here:
        loadTransactionsTable();
        CardLayout cardLayout = (CardLayout) mainScreen.getLayout();
        cardLayout.show(mainScreen, "transactionsPanel");
    }//GEN-LAST:event_btnTransactionsActionPerformed

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        // TODO add your handling code here:
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "loginPanel");
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void categoriesAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoriesAddButtonActionPerformed
        // TODO add your handling code here:
        showSaveCategoryDialog();
    }//GEN-LAST:event_categoriesAddButtonActionPerformed

    private void suppliersAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suppliersAddButtonActionPerformed
        // TODO add your handling code here:
        showSaveSupplierDialog();
    }//GEN-LAST:event_suppliersAddButtonActionPerformed

    private void itemsAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemsAddButtonActionPerformed
        // TODO add your handling code here:
        showSaveItemDialog();
    }//GEN-LAST:event_itemsAddButtonActionPerformed

    private void btnPurchasesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPurchasesActionPerformed
        // TODO add your handling code here:
        cmbSuppliers.setSelectedIndex(0);
        loadPurchasesTable((String) cmbSuppliers.getSelectedItem());
        loadSuppliers();
        cmbSuppliers.setSelectedIndex(0);
        CardLayout cardLayout = (CardLayout) mainScreen.getLayout();
        cardLayout.show(mainScreen, "purchasesPanel");
    }//GEN-LAST:event_btnPurchasesActionPerformed

    private void purchasesAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchasesAddButtonActionPerformed
        // TODO add your handling code here:
        showSavePurchaseDialog();
    }//GEN-LAST:event_purchasesAddButtonActionPerformed

    private void lpLoginKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lpLoginKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lpLoginKeyPressed

    private void cmbCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriesActionPerformed
        // TODO add your handling code here:
        loadItemsTable((String) cmbCategories.getSelectedItem());
    }//GEN-LAST:event_cmbCategoriesActionPerformed

    private void showAllItemsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllItemsButtonActionPerformed
        // TODO add your handling code here:
        cmbCategories.setSelectedIndex(0);
        loadItemsTable((String) cmbCategories.getSelectedItem());
    }//GEN-LAST:event_showAllItemsButtonActionPerformed

    private void cmbSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSuppliersActionPerformed
        // TODO add your handling code here:
        loadPurchasesTable((String) cmbSuppliers.getSelectedItem());
    }//GEN-LAST:event_cmbSuppliersActionPerformed

    private void showAllPurchasesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllPurchasesButtonActionPerformed
        // TODO add your handling code here:
        cmbSuppliers.setSelectedIndex(0);
        loadPurchasesTable((String) cmbSuppliers.getSelectedItem());
    }//GEN-LAST:event_showAllPurchasesButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLaf.registerCustomDefaultsSource("final_exam");
        FlatMacDarkLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnItems;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnPurchases;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JPanel buttonContainer;
    private javax.swing.JPanel buttonContainer1;
    private javax.swing.JPanel buttonContainer2;
    private javax.swing.JPanel buttonContainer3;
    private javax.swing.JPanel buttonContainer4;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JButton categoriesAddButton;
    private javax.swing.JPanel categoriesPanel;
    private javax.swing.JTable categoriesTable;
    private javax.swing.JComboBox<String> cmbCategories;
    private javax.swing.JComboBox<String> cmbSuppliers;
    private javax.swing.JPanel ctContainer;
    private javax.swing.JPanel ctNorth;
    private javax.swing.JPanel ctPane;
    private javax.swing.JScrollPane ctScroll;
    private javax.swing.JPanel itContainer;
    private javax.swing.JPanel itNorth;
    private javax.swing.JPanel itPane;
    private javax.swing.JScrollPane itScroll;
    private javax.swing.JButton itemsAddButton;
    private javax.swing.JPanel itemsPanel;
    private javax.swing.JTable itemsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JTextField lpEmailField;
    private javax.swing.JButton lpLogin;
    private javax.swing.JPasswordField lpPasswordField;
    private javax.swing.JButton lpRegister;
    private javax.swing.JPanel mainMenu;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainScreen;
    private javax.swing.JLabel nameDisplay;
    private javax.swing.JPanel ptContainer;
    private javax.swing.JPanel ptNorth;
    private javax.swing.JPanel ptPane;
    private javax.swing.JScrollPane ptScroll;
    private javax.swing.JButton purchasesAddButton;
    private javax.swing.JPanel purchasesPanel;
    private javax.swing.JTable purchasesTable;
    private javax.swing.JPanel registerPanel;
    private javax.swing.JTextField rpEmailField;
    private javax.swing.JTextField rpFirstNameField;
    private javax.swing.JTextField rpLastNameField;
    private javax.swing.JButton rpLogin;
    private javax.swing.JPasswordField rpPasswordField;
    private javax.swing.JButton rpRegister;
    private javax.swing.JButton showAllItemsButton;
    private javax.swing.JButton showAllPurchasesButton;
    private javax.swing.JPanel stContainer;
    private javax.swing.JPanel stNorth;
    private javax.swing.JPanel stPane;
    private javax.swing.JScrollPane stScroll;
    private javax.swing.JButton suppliersAddButton;
    private javax.swing.JPanel suppliersPanel;
    private javax.swing.JTable suppliersTable;
    private javax.swing.JLabel title;
    private javax.swing.JLabel title1;
    private javax.swing.JLabel title2;
    private javax.swing.JLabel title3;
    private javax.swing.JLabel title4;
    private javax.swing.JPanel transactionsPanel;
    private javax.swing.JTable transactionsTable;
    private javax.swing.JPanel ttContainer;
    private javax.swing.JPanel ttNorth;
    private javax.swing.JPanel ttPane;
    private javax.swing.JScrollPane ttScroll;
    // End of variables declaration//GEN-END:variables
}
