package com.codelovers.quanonghau.help;

import com.codelovers.quanonghau.models.Product;
import com.codelovers.quanonghau.models.ProductImage;
import com.codelovers.quanonghau.utils.FileUploadUtil;
import com.codelovers.quanonghau.utils.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductSaveHelper {

    public static void setMainImageName(MultipartFile mainImageFile, Product product) {
        if (!mainImageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageFile.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    // Cập nhật các extraImage ra 1 Set mới
    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images); // Vì giá trị ProductImage lưu trong Set, lên khi thay đổi thì chúng sẽ bị tác động
    }

    public static void setNewExtraImageNames(List<MultipartFile> extraImageFiles, Product product) {
        if (extraImageFiles.size() > 0) {
            for (MultipartFile multipartFile : extraImageFiles) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    if (!product.containsImageName(fileName)) { // Check image exit
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        Integer id = 0;

        for (int count = 0; count < detailNames.length; count++) {

            if (detailIDs != null) { // == Why detailsIDs.length() >0 not work
                id = Integer.parseInt(detailIDs[count]);
            }

            String name = detailNames[count];
            String value = detailValues[count];

            if (id != 0) {
                product.addDetails(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }
    }

    public static void saveUploadImages(MultipartFile mainImage, List<MultipartFile> extraImage, Product savedProduct) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "images/product-photo/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }

        if (extraImage.size() > 0) {
            String uploadDir = "images/product-photo/" + savedProduct.getId() + "/extras/";

            for (MultipartFile multipartFile : extraImage) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    public static void saveUploadQRCode(Product savedProduct, int height, int width) throws IOException, WriterException {
        // Using when create or save Product
        String uploadDir = "images/product-photo/" + savedProduct.getId() + "/qrcode/";

        FileUploadUtil.cleanDir(uploadDir);

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filePath = uploadDir + savedProduct.getQrCodeImage();
        String text = savedProduct.getId() +":"+ savedProduct.getName();

        QRCodeGenerator.generateQRCodeImage(text, width, height, filePath);
    }

    public static void deleteExtraImagesWereRemovedOnForm(Product product) { // Xóa ảnh trong folder
        String extraImageDir = "images/product-photo/" + product.getId() + "/extras/";

        Path dir = Paths.get(extraImageDir);

        try {
            Files.list(dir).forEach(file -> {
                String fileName = file.toFile().getName();

                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not list directory: " + dir);
        }
    }
}
