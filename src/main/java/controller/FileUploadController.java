package controller;

import annotation.Autowired;
import annotation.Controller;
import annotation.RequestMapping;
import annotation.RequestParam;
import serviceImpl.FileUploadServiceImpl;
import view.View;

import java.io.InputStream;

@Controller(value = "/fileUpload")
public class FileUploadController {
    @Autowired
    FileUploadServiceImpl uploadService;

    @RequestMapping("/fileUpload")
    public View fileUpload(InputStream fileSourceStream){
        try {
            uploadService.uploadFile(fileSourceStream);
            View view = new View("index.jsp");
            view.addAttribute("status", "upload success");
            return view;
        } catch (Exception e) {
            View view = new View("index.jsp");
            view.addAttribute("status", "upload failed");
            return view;
        }
    }

    @RequestMapping("/index")
    public View index() {
        return new View("/WEB-INF/index.jsp");
    }
}
