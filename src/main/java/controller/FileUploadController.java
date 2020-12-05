package controller;

import annotation.Autowired;
import annotation.Controller;
import annotation.RequestMapping;
import serviceImpl.FileUploadServiceImpl;

import java.io.FileInputStream;

@Controller
public class FileUploadController {
    @Autowired
    FileUploadServiceImpl uploadService;

    @RequestMapping("/fileUpload")
    public String fileUpload(String filePath){
        try {
            uploadService.uploadFile(new FileInputStream(filePath));
            return "upload success";
        } catch (Exception e) {
            e.getStackTrace();
            return "upload failed";
        }
    }
}
