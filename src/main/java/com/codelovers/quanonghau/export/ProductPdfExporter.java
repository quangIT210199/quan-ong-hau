package com.codelovers.quanonghau.export;

import com.codelovers.quanonghau.models.Product;
import com.codelovers.quanonghau.utils.AbstractExporter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductPdfExporter extends AbstractExporter {
    // Need in ProductPdfExporter.java
    public void exportQRCode(Product product, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/pdf", ".pdf", "productQR_");

        String filePath = "images/product-photo/" + product.getId() +"/qrcode/QRCode.png";
        Document document = new Document(PageSize.A5);
        try {
            PdfWriter.getInstance(document, response.getOutputStream()); // This getOutputStream() write binary data to response
            document.open();
            Image qrCode = Image.getInstance(filePath);
            document.add(qrCode);
        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();
    }
}
