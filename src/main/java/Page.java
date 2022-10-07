import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
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
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Page extends JFrame {
    JFrame jFrame = new JFrame();
    public static JTextArea workArea;
    private JScrollPane scrollPane;
    private FileDialog saveDia;
    private static String str="";
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

        fileItem_open.addActionListener(e -> open());

        fileItem_new.addActionListener(e -> New());

        fileItem_exit.addActionListener(e -> exit());

        helpItem_about.addActionListener(e -> about());

        editItem_copy.addActionListener(e -> Copy());

        editItem_paste.addActionListener(e -> Paste());

        editItem_cut.addActionListener(e -> Cut());

        fileItem_save.addActionListener(e -> fileItem_save());
    }

    void open() {
        JFileChooser jFileChooser = new JFileChooser();
        int chose = jFileChooser.showOpenDialog(null);
        if (chose == JFileChooser.CANCEL_OPTION) {
            return ;
        }
        File F = jFileChooser.getSelectedFile();
        workArea.setText("");
        jFrame.setTitle(F.getName());
        if(F.getName().contains(".rtf")){
            openRtf(F);
        }else if(F.getName().contains(".odt")){
            openOdt(F);
        }else {
            openElse(F);
        }
    }

    void openRtf(File F){
        DefaultStyledDocument styleDoc = new DefaultStyledDocument();
        String result;
        try {
            InputStream inputStream = new FileInputStream(F);
            try {
                new RTFEditorKit().read(inputStream,styleDoc,0);
                result = new String(styleDoc.getText(0,styleDoc.getLength()).getBytes("ISO8859-1"),"GBK");
            } catch (IOException | BadLocationException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        workArea.setText(result);
    }

    void openOdt(File F){
        try {
            ZipFile zipFile = new ZipFile(F);
            org.w3c.dom.Document doc = null;
            Enumeration<?> entries = zipFile.entries();
            ZipEntry entry;
            while (entries.hasMoreElements()){
                entry = (ZipEntry)entries.nextElement();
                if(entry.getName().equals("content.xml")){
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
                    for (int a = 0; a < list.getLength(); a++){
                        Node node =list.item(a);
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

    private static void getText(org.w3c.dom.Node node) {
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

    void openElse(File F){
        if (F != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(F));
                String line;
                while((line = br.readLine()) != null){
                    workArea.append(line + "\r\n");
                }
            }catch (IOException er1){
                throw new RuntimeException("Failed!！");
            }
        }
    }


    void New() {
        new Page();
    }

    void exit(){
        jFrame.dispose();
    }


    void Cut(){workArea.cut();}

    void Copy(){
        workArea.copy();
    }

    void Paste(){
        workArea.paste();
    }

    void about(){
        JOptionPane.showMessageDialog(null,"HEFEIFANCHWENRANSHINIUMA","About Us",JOptionPane.PLAIN_MESSAGE);

    }
    void fileItem_save(){
        saveDia = new FileDialog(this,"save as(A)",FileDialog.SAVE);
        File fileS = null;
        saveDia.setVisible(true);
        String dirPath = saveDia.getDirectory();
        String fileName = saveDia.getFile();
        if(fileName.contains(".pdf")){
            savepdf(dirPath,fileName,fileS);
            return;
        }
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
            throw new RuntimeException("file saved failed!");
        }
    }
    void savepdf(String dirPath,String fileName,File fileS1){

        if(dirPath == null || fileName == null) {
            return;
        }
        fileS1 = new File(dirPath,fileName);
        try {
            String s=workArea.getText();
            String[] strings = s.split("\n");
            PDDocument document=new PDDocument();
            PDPage my_page=new PDPage(PDRectangle.A4);
            document.addPage(my_page);
            PDFont font= PDType0Font.load(document, new File("C:/Windows/Fonts/Arial.ttf"));
            PDPageContentStream contentStream = new PDPageContentStream(document,my_page);
            my_page.getResources().add(font);
            //set font for pdf
            workArea.getText(0,1);
            for(int i=0;i<strings.length;i++){
                contentStream.beginText();
                contentStream.setFont(font,10);
                contentStream.newLineAtOffset(10,  820-i*20);
                contentStream.showText(strings[i]);
                contentStream.endText();
            }
            contentStream.close();
            document.save(fileS1);
            document.close();
        }catch (IOException | BadLocationException er){
            throw new RuntimeException("file saved failed");
        }
    }

}
