import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Page extends JFrame {
    JFrame jFrame = new JFrame();
    public static JTextArea workArea;
    private JScrollPane scrollPane;
    private FileDialog saveDia;
    Page() {
        init();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    void init(){
        jFrame.setTitle("Text Editor");
        jFrame.setBounds(400,100,500,700);
        JMenuBar menuBar = new JMenuBar();
        jFrame.setJMenuBar(menuBar);

        JMenu menu_file = new JMenu("File");
        JMenu menu_edit = new JMenu("Edit");
        JMenu menu_view = new JMenu("View");
        JMenu menu_help = new JMenu("Help");
        menuBar.add(menu_file);
        menuBar.add(menu_edit);
        menuBar.add(menu_view);
        menuBar.add(menu_help);

        workArea = new JTextArea();
        scrollPane = new JScrollPane(workArea);
        jFrame.add(scrollPane);
        JMenuItem fileItem_new = new JMenuItem("new");
        JMenuItem fileItem_open = new JMenuItem("open");
        JMenuItem fileItem_save = new JMenuItem("save");
        JMenuItem fileItem_print = new JMenuItem("print");
        JMenuItem fileItem_exit = new JMenuItem("exit");
        menu_file.add(fileItem_new);
        menu_file.add(fileItem_open);
        menu_file.add(fileItem_save);
        menu_file.add(fileItem_print);
        menu_file.add(fileItem_exit);

        JMenuItem editItem_search = new JMenuItem("search");
        JMenuItem editItem_copy = new JMenuItem("copy");
        JMenuItem editItem_paste = new JMenuItem("paste");
        JMenuItem editItem_cut = new JMenuItem("cut");
        menu_edit.add(editItem_search);
        menu_edit.add(editItem_copy);
        menu_edit.add(editItem_paste);
        menu_edit.add(editItem_cut);

        JMenuItem viewItem_TD = new JMenuItem("Time and Date");
        menu_view.add(viewItem_TD);

        JMenuItem helpItem_about = new JMenuItem("about");
        menu_help.add(helpItem_about);
        fileItem_save.addActionListener(e -> fileItem_save());

    }
    void fileItem_save(){
        saveDia = new FileDialog(this,"save as(A)",FileDialog.SAVE);
        File fileS = null;

        saveDia.setVisible(true);
        String dirPath = saveDia.getDirectory();
        String fileName = saveDia.getFile();
        if (!fileName.contains(".txt")) {
            fileName += ".txt";
        }
        if(dirPath == null || fileName == null) {
            return;
        }
        fileS = new File(dirPath,fileName);

        try{
            BufferedWriter bufw = new BufferedWriter(new FileWriter(fileS));
            String text = workArea.getText();
            bufw.write(text);
            bufw.close();
        }catch(IOException er){
            throw new RuntimeException("file saved failed");
        }
    }
}
