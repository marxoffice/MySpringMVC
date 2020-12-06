package controller;

import annotation.Autowired;
import annotation.Controller;
import annotation.RequestMapping;
import service.FileUploadService;
import serviceImpl.FileUploadServiceImpl;
import view.View;

import java.io.InputStream;

@Controller(value = "/fileUpload")
public class FileUploadController {
//    @Autowired
//    FileUploadService uploadService;

    @RequestMapping("/fileUpload")
    public View fileUpload(InputStream fileSourceStream){
        FileUploadServiceImpl service = new FileUploadServiceImpl();
        boolean status = FileUploadServiceImpl.uploadFile(fileSourceStream);
        View view = new View("/WEB-INF/result.jsp");
        if (status) {
            view.addAttribute("status", "upload success");
            return view;
        } else {
            view.addAttribute("status", "upload failed");
            return view;
        }
    }

    @RequestMapping("/index")
    public View index() {
        return new View("/WEB-INF/index.jsp");
    }
}
