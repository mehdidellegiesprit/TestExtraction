package com.PFE.TestExtraction;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.utilities.PdfTable;
import com.spire.pdf.utilities.PdfTableExtractor;

import java.io.FileWriter;

public class ExtractTableData {
    public static void main(String []args) throws Exception {
        String path = "C:\\Users\\mehdi\\Desktop\\mehdi_test_extract_pfe\\TestExtraction\\Ext1.pdf";
        //Load a sample PDF document
        PdfDocument pdf = new PdfDocument(path);

        //Create a StringBuilder instance
        StringBuilder builder = new StringBuilder();
        //Create a PdfTableExtractor instance
        PdfTableExtractor extractor = new PdfTableExtractor(pdf);

        //Loop through the pages in the PDF
        for (int pageIndex = 0; pageIndex < pdf.getPages().getCount(); pageIndex++) {
            //Extract tables from the current page into a PdfTable array
            PdfTable[] tableLists = extractor.extractTable(pageIndex);

            //If any tables are found
            if (tableLists != null && tableLists.length > 0) {
                //Loop through the tables in the array
                for (PdfTable table : tableLists) {
                    //Loop through the rows in the current table
                    for (int i = 0; i < table.getRowCount(); i++) {
                        //Loop through the columns in the current table
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            //Extract data from the current table cell and append to the StringBuilder 
                            String text = table.getText(i, j);
                            if (text.equals("")){
                                text="************";
                            }
                            builder.append(text + " | ");
                        }
                        builder.append("\r\n");
                    }
                }
            }
        }

        //Write data into a .txt document
        FileWriter fw = new FileWriter("ExtractTable.txt");
        System.out.println(fw);
        fw.write(builder.toString());
        fw.flush();
        fw.close();
    }
}