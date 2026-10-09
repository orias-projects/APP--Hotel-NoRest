package view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FormUIFactory {

    public static JDialog createFormDialog(String title, JPanel inputPanel, String confirmText, ActionListener confirmAction, Component parent) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel();
        JButton btnConfirm = UIFactory.createBtnPrimary(confirmText);
        JButton btnCancel = UIFactory.createBtnRefresh("Hủy");

        pnlButtons.add(btnConfirm);
        pnlButtons.add(btnCancel);

        dialog.add(pnlButtons, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnConfirm.addActionListener(confirmAction);

        // Tính kích thước: giả sử width fixed 600, height dựa trên số rows của inputPanel
        int rows = ((GridLayout) inputPanel.getLayout()).getRows();
        int height = rows * 50 + 150; // 50 per row + padding + buttons
        dialog.setSize(600, height);

        dialog.setLocationRelativeTo(parent);
        return dialog;
    }
}