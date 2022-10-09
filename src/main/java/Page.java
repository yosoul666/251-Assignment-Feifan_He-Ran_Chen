import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Page extends JFrame {
    JFrame jFrame = new JFrame();
    public static org.fife.ui.rsyntaxtextarea.RSyntaxTextArea workArea;
    private RTextScrollPane scrollPane;

    private JMenuItem Time;

    private FileDialog saveDia;

    private static String str = "";
    private int index = 0;

    public static File file;

    Page() {
        init();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    void init() {
        jFrame.setTitle("Text Editor");
        jFrame.setBounds(400, 100, 500, 700);
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

        workArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        scrollPane = new RTextScrollPane(workArea);
        workArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
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

        fileItem_open.addActionListener(e -> open());

        fileItem_new.addActionListener(e -> New());

        fileItem_exit.addActionListener(e -> exit());

        helpItem_about.addActionListener(e -> about());

        editItem_copy.addActionListener(e -> Copy());

        editItem_paste.addActionListener(e -> Paste());

        editItem_cut.addActionListener(e -> Cut());

        fileItem_save.addActionListener(e -> fileItem_save());

        fileItem_print.addActionListener(e -> {
            try {
                printer();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        editItem_search.addActionListener(e -> search());

        Time = new JMenuItem();
        class TimeActionListener implements ActionListener {
            public TimeActionListener() {
                javax.swing.Timer t = new javax.swing.Timer(1000, this);
                t.start();
            }

            @Override
            public void actionPerformed(ActionEvent ae) {
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String currentTime = sdf.format(d);
                Time.setText(currentTime);
            }
        }
        Time.addActionListener(new TimeActionListener());
        menuBar.add(Time);
    }

    void search() {
        JDialog jDialog = new JDialog(jFrame);
        jDialog.setBounds(500, 200, 380, 100);
        //初始化jDialog
        jDialogInit(jDialog);
        jDialog.setVisible(true);
        jDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    void jDialogInit(JDialog jDialog) {
        jDialog.setTitle("Search");
        jDialog.setLayout(null);
        //Create a plate
        JPanel pane = new JPanel();
        //Add plates to the box
        jDialog.add(pane);
        //Set the layout of the pane
        pane.setLayout(new FlowLayout(FlowLayout.LEFT));
        pane.setBounds(0, 0, 400, 70);
        //Plate contents
        //Find
        JLabel lookFor = new JLabel("Find  content    ");
        JTextField lookFor_field = new JTextField(17);
        JButton jb1 = new JButton("Find Next");


        //Add elements to the pane
        pane.add(lookFor);
        pane.add(lookFor_field);
        pane.add(jb1);
        JButton exit = new JButton("Cancel");
        pane.add(exit);


        jb1.addActionListener(e -> {
            String word = lookFor_field.getText();
            int x = workArea.getText().indexOf(word, index);
            //if(not find) return
            if (x == -1) {
                JOptionPane.showMessageDialog(null, "No searching", "Warning Message", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            //if find index+length
            int len = word.length();
            index = x + len;
            //Set Shadows
            workArea.setSelectionStart(x);
            workArea.setSelectionEnd(x + len);
        });

        exit.addActionListener(e -> {
            jDialog.dispose();
        });

    }
        void open () {
            JFileChooser jFileChooser = new JFileChooser();
            int chose = jFileChooser.showOpenDialog(null);
            if (chose == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File F = jFileChooser.getSelectedFile();
            workArea.setText("");
            jFrame.setTitle(F.getName());
            if (F.getName().contains(".rtf")) {
                openRtf(F);
            } else if (F.getName().contains(".odt")) {
                openOdt(F);
            } else {
                openElse(F);
            }
        }

        void openRtf (File F){
            DefaultStyledDocument styleDoc = new DefaultStyledDocument();
            String result;
            try {
                InputStream inputStream = new FileInputStream(F);
                try {
                    new RTFEditorKit().read(inputStream, styleDoc, 0);
                    result = new String(styleDoc.getText(0, styleDoc.getLength()).getBytes("ISO8859-1"), "GBK");
                } catch (IOException | BadLocationException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            workArea.setText(result);
        }

        void openOdt (File F){
            try {
                ZipFile zipFile = new ZipFile(F);
                org.w3c.dom.Document doc = null;
                Enumeration<?> entries = zipFile.entries();
                ZipEntry entry;
                while (entries.hasMoreElements()) {
                    entry = (ZipEntry) entries.nextElement();
                    if (entry.getName().equals("content.xml")) {
                        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                        domFactory.setNamespaceAware(true);
                        DocumentBuilder docBuilder = null;
                        try {
                            docBuilder = domFactory.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            doc = docBuilder.parse(zipFile.getInputStream(entry));
                        } catch (SAXException e) {
                            throw new RuntimeException(e);
                        }
                        NodeList list = doc.getElementsByTagName("text:p");
                        for (int a = 0; a < list.getLength(); a++) {
                            Node node = list.item(a);
                            getText(node);
                            workArea.setText(str);
                            str = "";
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static void getText (org.w3c.dom.Node node){
            if (node.getChildNodes().getLength() > 1) {
                NodeList childNodes = node.getChildNodes();
                for (int a = 0; a < childNodes.getLength(); a++) {
                    getText(node.getChildNodes().item(a));
                }
            } else {
                if (node.getNodeValue() != null) {
                    str = str + node.getNodeValue();
                }
                if (node.getFirstChild() != null) {
                    str = str + node.getFirstChild().getNodeValue();
                }
            }
        }

        void openElse (File F){
            if (F != null) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(F));
                    String line;
                    while ((line = br.readLine()) != null) {
                        workArea.append(line + "\r\n");
                    }
                } catch (IOException er1) {
                    throw new RuntimeException("Failed!！");
                }
            }
        }


        void New () {
            new Page();
        }

        void exit () {
            jFrame.dispose();
        }


        void Cut () {
            workArea.cut();
        }

        void Copy () {
            workArea.copy();
        }

        void Paste () {
            workArea.paste();
        }

        void about () {
            JOptionPane.showMessageDialog(null, "By HeFeiFan and ChenRan", "About Us", JOptionPane.PLAIN_MESSAGE);
        }
        void fileItem_save () {
            saveDia = new FileDialog(this, "save as(A)", FileDialog.SAVE);
            File fileS = null;
            saveDia.setVisible(true);
            String dirPath = saveDia.getDirectory();
            String fileName = saveDia.getFile();
            if (fileName.contains(".pdf")) {
                savepdf(dirPath, fileName, fileS);
                return;
            }
            if (!fileName.contains(".txt")) {
                fileName += ".txt";
            }

            if (dirPath == null || fileName == null) {
                return;
            }
            fileS = new File(dirPath, fileName);
            try {
                BufferedWriter bufw = new BufferedWriter(new FileWriter(fileS));
                String text = workArea.getText();
                bufw.write(text);
                bufw.close();
            } catch (IOException er) {
                throw new RuntimeException("file saved failed!");
            }
        }
        void savepdf (String dirPath, String fileName, File fileS1){

            if (dirPath == null || fileName == null) {
                return;
            }
            fileS1 = new File(dirPath, fileName);
            try {
                String s = workArea.getText();
                String[] strings = s.split("\n");
                PDDocument document = new PDDocument();
                PDPage my_page = new PDPage(PDRectangle.A4);
                document.addPage(my_page);
                PDFont font = PDType0Font.load(document, new File("C:/Windows/Fonts/Arial.ttf"));
                PDPageContentStream contentStream = new PDPageContentStream(document, my_page);
                my_page.getResources().add(font);
                //set font for pdf
                workArea.getText(0, 1);
                for (int i = 0; i < strings.length; i++) {
                    contentStream.beginText();
                    contentStream.setFont(font, 10);
                    contentStream.newLineAtOffset(10, 820 - i * 20);
                    contentStream.showText(strings[i]);
                    contentStream.endText();
                }
                contentStream.close();
                document.save(fileS1);
                document.close();
            } catch (IOException | BadLocationException er) {
                throw new RuntimeException("file saved failed");
            }
        }

        void printer () throws Exception {
            file = new File("D:", "sjofj.pdf");
            String s = workArea.getText();
            String[] strings = s.split("\n");
            PDDocument document = new PDDocument();
            PDPage my_page = new PDPage(PDRectangle.A4);
            document.addPage(my_page);
            PDFont font = PDType0Font.load(document, new File("C:/Windows/Fonts/Arial.ttf"));
            PDPageContentStream contentStream = new PDPageContentStream(document, my_page);
            my_page.getResources().add(font);

            //set font for pdf
            workArea.getText(0, 1);
            for (int i = 0; i < strings.length; i++) {
                contentStream.beginText();
                contentStream.setFont(font, 10);
                contentStream.newLineAtOffset(10, 820 - i * 20);
                contentStream.showText(strings[i]);
                contentStream.endText();
            }
            contentStream.close();
            document.save(file);
            document.close();
            print print = new print();
            print.PDFprint();
            file.delete();
        }
}
