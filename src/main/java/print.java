import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import javax.swing.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.IOException;

public class print {

    public  void PDFprint() throws Exception {
        String printerName="Microsoft Print to PDF";
        PDDocument document = null;
        try {
            document = PDDocument.load(Page.file);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(Page.file.getName());
            if (printerName != null) {
                //set up printers
                //printers connected to this computer
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if(printServices == null || printServices.length == 0) {
                    System.out.print("Printing failed, no available printer found, please check.");
                    JOptionPane.showMessageDialog(null,"Printing failed","Warning Message",JOptionPane.PLAIN_MESSAGE);
                    return ;
                }
                PrintService printService = null;
                // printer
                for (int i = 0;i < printServices.length; i++) {
                    if (printServices[i].getName().contains(printerName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if(printService!=null){
                    printJob.setPrintService(printService);
                }else{
                    System.out.print("Can‘t printing," + printerName + "printer,please checkout");
                    return ;
                }
            }
            // paper and scale
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            // multi-page printing
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            // print orientation
            pageFormat.setOrientation(PageFormat.PORTRAIT);
            //Longitudinal
            pageFormat.setPaper(getPaper());
            // the paper
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);
            //the number of copies printed
            // print properties
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX);

            // single and double pages
            printJob.print(pars);
        }finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static Paper getPaper() {
        Paper paper = new Paper();

        int width = 595;
        int height = 842;

        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);

        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }
}
