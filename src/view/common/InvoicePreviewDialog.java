package view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class InvoicePreviewDialog extends JDialog {

    private InvoicePrinter printer;
    private PrinterJob printerJob;
    private PageFormat pageFormat;

    public InvoicePreviewDialog(Window owner, InvoicePrinter printer) {
        super(owner, "Xem trước hóa đơn", ModalityType.APPLICATION_MODAL);
        this.printer = printer;
        this.printerJob = PrinterJob.getPrinterJob();
        this.pageFormat = printerJob.defaultPage();

        initComponents();

        // Set size relative to screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.5);
        int height = (int) (screenSize.height * 0.85);
        setSize(width, height);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Preview Panel
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Scale to fit width if needed, or just render as is
                // For simplicity, we render at 100% scale but centered

                // White background for paper
                g2d.setColor(Color.WHITE);
                int paperWidth = (int) pageFormat.getWidth();
                int paperHeight = (int) pageFormat.getHeight();
                int x = (getWidth() - paperWidth) / 2;
                int y = 20;

                g2d.fillRect(x, y, paperWidth, paperHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, paperWidth, paperHeight);

                // Translate to paper origin
                g2d.translate(x, y);

                try {
                    printer.print(g2d, pageFormat, 0);
                } catch (PrinterException e) {
                    e.printStackTrace();
                }
            }
        };
        previewPanel.setBackground(Color.LIGHT_GRAY);
        previewPanel
                .setPreferredSize(new Dimension((int) pageFormat.getWidth() + 50, (int) pageFormat.getHeight() + 50));

        add(new JScrollPane(previewPanel), BorderLayout.CENTER);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = UIFactory.createBtnPrimary("In (Print)");
        JButton btnClose = UIFactory.createButton("Đóng (Close)");
        btnClose.setBackground(Color.GRAY);

        btnPrint.addActionListener(e -> print());
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void print() {
        printerJob.setPrintable(printer);
        boolean doPrint = printerJob.printDialog();
        if (doPrint) {
            try {
                printerJob.print();
                dispose();
            } catch (PrinterException e) {
                UIFactory.showError(this, "Lỗi khi in: " + e.getMessage());
            }
        }
    }
}
