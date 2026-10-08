package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import controller.ThietBiPhongController;
import model.ThietBiPhong;
import model.Phong;
import model.ThietBi;
import service.PhongService;
import service.ThietBiService;
import view.common.UIFactory;

public class PanelThietBiPhong extends JPanel {

    private ThietBiPhongController controller = new ThietBiPhongController();
    private PhongService phongService = new PhongService();
    private ThietBiService thietBiService = new ThietBiService();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtSoLuong;
    private JComboBox<Phong> cboPhong;
    private JComboBox<ThietBi> cboThietBi;
    private JComboBox<String> cboTrangThai;

    private JTextField txtSearch;
    private JComboBox<String> cboFilter;

    private List<ThietBiPhong> dataList;
    private Map<Integer, String> mapPhong = new HashMap<>();
    private Map<Integer, String> mapThietBi = new HashMap<>();

    public PanelThietBiPhong() {
        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("THIẾT BỊ PHÒNG");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Phòng", "Thiết bị"});
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(3, 4);
        txtID = UIFactory.createTextField(0); txtID.setEditable(false);
        txtSoLuong = UIFactory.createTextField(0);

        List<Phong> listPhong = phongService.getAll();
        List<ThietBi> listTB = thietBiService.getAll();

        for (Phong p : listPhong) mapPhong.put(p.getMaPhong(), p.getSoPhong());
        for (ThietBi tb : listTB) mapThietBi.put(tb.getMaThietBi(), tb.getTenThietBi());

        cboPhong = new JComboBox<>(listPhong.toArray(new Phong[0]));
        cboThietBi = new JComboBox<>(listTB.toArray(new ThietBi[0]));
        cboTrangThai = new JComboBox<>(new String[]{"Tốt","Hỏng","Bảo trì"});

        pnlInput.add(UIFactory.createLabel("ID:")); pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Phòng:")); pnlInput.add(cboPhong);
        pnlInput.add(UIFactory.createLabel("Thiết bị:")); pnlInput.add(cboThietBi);
        pnlInput.add(UIFactory.createLabel("Số lượng:")); pnlInput.add(txtSoLuong);
        pnlInput.add(UIFactory.createLabel("Trạng thái:")); pnlInput.add(cboTrangThai);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{"ID", "Phòng", "Thiết bị", "Số lượng", "Trạng thái"});
        tbl = UIFactory.createTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row,0).toString());

                    int maPhong = getKeyByValue(mapPhong, model.getValueAt(row,1).toString());
                    int maTB = getKeyByValue(mapThietBi, model.getValueAt(row,2).toString());

                    setSelectedCbo(cboPhong, maPhong);
                    setSelectedCbo(cboThietBi, maTB);

                    txtSoLuong.setText(model.getValueAt(row,3).toString());
                    cboTrangThai.setSelectedItem(model.getValueAt(row,4).toString());
                }
            }
        });

        // ==================== BUTTON PANEL ====================
        JPanel pnlBottom = UIFactory.createPanelBase();
        JButton btnAdd = UIFactory.createBtnPrimary("Thêm");
        JButton btnUpdate = UIFactory.createBtnUpdate("Sửa");
        JButton btnDelete = UIFactory.createBtnDelete("Xóa");
        JButton btnRefresh = UIFactory.createBtnRefresh("Làm mới");

        pnlBottom.add(btnAdd);
        pnlBottom.add(btnUpdate);
        pnlBottom.add(btnDelete);
        pnlBottom.add(btnRefresh);

        add(pnlBottom, BorderLayout.SOUTH);

        // ==================== EVENTS ====================
        txtSearch.addKeyListener(new KeyAdapter(){ public void keyReleased(KeyEvent e){ filterData(); }});
        cboFilter.addActionListener(e -> filterData());

        btnAdd.addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnRefresh.addActionListener(e -> clearForm());

        loadData();
    }

    // ==================== LOAD DATA ====================
    private void loadData() {
        dataList = controller.getAll();
        filterData();
    }

    // ==================== FILTER DATA ====================
    private void filterData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String type = cboFilter.getSelectedItem().toString();
        model.setRowCount(0);

        for(ThietBiPhong tbp : dataList){
            String tenPhong = mapPhong.get(tbp.getMaPhong());
            String tenTB = mapThietBi.get(tbp.getMaThietBi());

            boolean match = switch (type){
                case "Phòng" -> tenPhong.toLowerCase().contains(keyword);
                case "Thiết bị" -> tenTB.toLowerCase().contains(keyword);
                default -> tenPhong.toLowerCase().contains(keyword) || tenTB.toLowerCase().contains(keyword);
            };
            if(match){
                model.addRow(new Object[]{
                        tbp.getMaThietBiPhong(),
                        tenPhong,
                        tenTB,
                        tbp.getSoLuong(),
                        tbp.getTrangThai()
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd(){
        ThietBiPhong tbp = new ThietBiPhong(
                0,
                ((Phong)cboPhong.getSelectedItem()).getMaPhong(),
                ((ThietBi)cboThietBi.getSelectedItem()).getMaThietBi(),
                Integer.parseInt(txtSoLuong.getText()),
                cboTrangThai.getSelectedItem().toString()
        );
        if(controller.add(tbp)){
            UIFactory.showMsg(this,"Thêm thành công");
            loadData();
        } else UIFactory.showError(this,"Thêm thất bại");
    }

    // ==================== UPDATE ====================
    private void doUpdate(){
        if(txtID.getText().isEmpty()){ UIFactory.showError(this,"Chọn dòng cần sửa!"); return; }
        ThietBiPhong tbp = new ThietBiPhong(
                Integer.parseInt(txtID.getText()),
                ((Phong)cboPhong.getSelectedItem()).getMaPhong(),
                ((ThietBi)cboThietBi.getSelectedItem()).getMaThietBi(),
                Integer.parseInt(txtSoLuong.getText()),
                cboTrangThai.getSelectedItem().toString()
        );
        if(controller.update(tbp)){
            UIFactory.showMsg(this,"Sửa thành công");
            loadData();
        } else UIFactory.showError(this,"Sửa thất bại");
    }

    // ==================== DELETE ====================
    private void doDelete(){
        if(txtID.getText().isEmpty()){ UIFactory.showError(this,"Chọn dòng cần xóa!"); return; }
        if(UIFactory.showConfirm(this,"Xóa thiết bị phòng này?")){
            if(controller.delete(Integer.parseInt(txtID.getText()))){
                UIFactory.showMsg(this,"Xóa thành công");
                loadData();
            } else UIFactory.showError(this,"Xóa thất bại");
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm(){
        txtID.setText("");
        cboPhong.setSelectedIndex(0);
        cboThietBi.setSelectedIndex(0);
        txtSoLuong.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtSearch.setText("");
    }

    // ==================== HELPER ====================
    private int getKeyByValue(Map<Integer,String> map, String value){
        for(Map.Entry<Integer,String> e : map.entrySet()){
            if(e.getValue().equals(value)) return e.getKey();
        }
        return -1;
    }

    private void setSelectedCbo(JComboBox<?> cbo, int id){
        for(int i=0;i<cbo.getItemCount();i++){
            Object item = cbo.getItemAt(i);
            if(item instanceof Phong && ((Phong)item).getMaPhong()==id){
                cbo.setSelectedIndex(i); break;
            } else if(item instanceof ThietBi && ((ThietBi)item).getMaThietBi()==id){
                cbo.setSelectedIndex(i); break;
            }
        }
    }
}