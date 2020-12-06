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

    /**
     * 文件上传，前端传递一个
     * @param fileSourceStream
     * @return
     */
    @RequestMapping("/fileUpload")
    public View fileUpload(InputStream fileSourceStream){
        FileUploadServiceImpl service = new FileUploadServiceImpl();
        boolean status = service.uploadFile(fileSourceStream);
        View view = new View("/WEB-INF/result.jsp");
        if (status) {
            view.addAttribute("status", "upload success");
        } else {
            view.addAttribute("status", "upload failed");
        }
        return view;
    }

    /**
     * 跳转至文件上传初始页面
     * @return
     */
    @RequestMapping("/index")
    public View index() {
        return new View("/WEB-INF/index.jsp");
    }
}
