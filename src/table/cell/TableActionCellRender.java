package table.cell;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Custom cell renderer for table actions.
 */
public class TableActionCellRender extends JPanel implements TableCellRenderer {
    private final PanelAction panelAction;
    private final boolean showEdit;
    private final boolean showDelete;
    private final boolean showView;

    public TableActionCellRender() {
        this(true, true, true);
    }
    
    public TableActionCellRender(boolean showEdit, boolean showDelete, boolean showView) {
        this.showEdit = showEdit;
        this.showDelete = showDelete;
        this.showView = showView;
        panelAction = new PanelAction();
        setLayout(new java.awt.BorderLayout());
        add(panelAction, java.awt.BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableCellRenderer defaultRenderer = table.getDefaultRenderer(Object.class);
        Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(c.getBackground());

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(c.getBackground());
        }

        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        panelAction.showEditButton(showEdit);
        panelAction.showDeleteButton(showDelete);
        panelAction.showViewButton(showView);
        
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getBackground() != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
        super.paintComponent(g);
    }
}