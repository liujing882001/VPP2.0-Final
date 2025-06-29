//package com.example.vvpweb.demand;
//
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.opencv.core.*;
//import org.opencv.imgproc.Imgproc;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//@RequestMapping("/energy")
//@CrossOrigin
//@Api(value = "能源专家", tags = {"能源专家"})
//@Slf4j
//public class ChatController {
//    //    @Value("${upload.path}")
//    private String uploadPath = "C:\\Users\\29634\\Desktop\\ceshidddd\\";
//    private String datapath = "D:\\work\\JetBrains\\project\\virtualPowerPlant\\CSPG_VPP\\vpp-web\\src\\main\\resources\\file";
//
//    public static void main(String[] args) {
//        log.info("结果：{}",ocr((MultipartFile) new File("C:\\Users\\29634\\Desktop\\xxxxxxxx.jpg")));
//    }
//
//    //    @PostMapping("/ocr")
//    public static String ocr(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return "上传的文件为空";
//        }
//        String uploadPath = "C:\\Users\\29634\\Desktop\\ceshidddd\\";
//
//        // 检查上传目录是否存在，如果不存在则创建
//        File uploadDir = new File(uploadPath);
//        if (!uploadDir.exists()) {
//            uploadDir.mkdirs();
//        }
//
//        // 获取文件名
//        String fileName = file.getOriginalFilename();
//        File uploadFile = new File(uploadPath + "ddd"+ fileName);
//        try {
//            // 保存上传的文件
//            file.transferTo(uploadFile);
//        } catch (IOException e) {
//            return "文件保存失败";
//        }
//
//        // 对图像进行预处理
//        Mat preprocessedImage = preprocessImage(uploadFile);
//
//        // 保存预处理后的图像
//        String preprocessedImagePath = uploadPath + fileName;
//        Imgcodecs.imwrite(preprocessedImagePath, preprocessedImage);
//
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("D:/work/JetBrains/project/virtualPowerPlant/CSPG_VPP/vpp-web/src/main/resources/file");
//        tesseract.setLanguage("chi_sim");
//
//        try {
//            String result = tesseract.doOCR(new File(preprocessedImagePath));
//            return result;
//        } catch (TesseractException e) {
//            return "";
//        } finally {
//            // 删除上传的文件和预处理后的图像
////            uploadFile.delete();
////            new File(preprocessedImagePath).delete();
//        }
//    }
//
//    // 图像预处理方法
//    private static Mat preprocessImage(File imageFile) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        // 读取图像文件
//        Mat src = Imgcodecs.imread(imageFile.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
//        if (src.empty()) {
//            System.err.println("无法读取图像文件：" + imageFile.getAbsolutePath());
//            return null;
//        }
//
//        // 反转颜色
////        Core.bitwise_not(src, src);
//
//        // 自适应阈值化
//        Mat binarized = new Mat();
//        Imgproc.adaptiveThreshold(src, binarized, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
//
//        // 去除水平线
//        Mat horizontal = binarized.clone();
//        int horizontalSize = horizontal.cols() / 30;
//        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalSize, 1));
//        Imgproc.erode(horizontal, horizontal, horizontalStructure);
//        Imgproc.dilate(horizontal, horizontal, horizontalStructure);
//
//        // 去除垂直线
//        Mat vertical = binarized.clone();
//        int verticalSize = vertical.rows() / 30;
//        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, verticalSize));
//        Imgproc.erode(vertical, vertical, verticalStructure);
//        Imgproc.dilate(vertical, vertical, verticalStructure);
//
//        // 合并图像以去除所有线条
//        Mat lines = new Mat();
//        Core.add(horizontal, vertical, lines);
//        Core.bitwise_not(lines, lines);
//        Core.bitwise_and(binarized, lines, binarized);
//
//        // 清理边界
//        int borderWidth = 5;
//        Rect roi = new Rect(new Point(borderWidth, borderWidth), new Size(binarized.cols() - 2 * borderWidth, binarized.rows() - 2 * borderWidth));
//        Mat cleaned = new Mat(binarized, roi);
//
//        return cleaned;
//    }
//}
