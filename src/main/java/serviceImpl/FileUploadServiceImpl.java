package serviceImpl;

import service.FileUploadService;

import java.io.*;

public class FileUploadServiceImpl {
    public FileUploadServiceImpl(){
        System.out.println("in FileUploadService");
    }
    /**
     * 文件上传
     */
//    @Override
    public static boolean uploadFile(InputStream fileSourceStream) {
        try {
//            request.setCharacterEncoding("utf-8");
//            response.setCharacterEncoding("utf-8");
//            response.setContentType("text/html;charset=utf-8");// 更改响应字符流使用的编码，还能告知浏览器用什么编码进行显示
//
//            // 从request中获取文本输入流信息
//            InputStream fileSourceStream = request.getInputStream();

            String tempFilePath = "/src/main/files/tempFile";

            // 设置临时文件，保存待上传的文本输入流
            File tempFile = new File(tempFilePath);

            // outputStream文件输出流指向这个tempFile
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            // 读取文件流
            byte [] buffer = new byte[1024];
            int n;
            while ((n = fileSourceStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }
            outputStream.close();
            fileSourceStream.close();

            // 获取上传文件的名称
            RandomAccessFile randomFile = new RandomAccessFile(tempFile, "r");
            randomFile.readLine();
            String str = randomFile.readLine();
            int start = str.lastIndexOf("=") + 2;
            int end = str.lastIndexOf("\"");
            String filename = str.substring(start, end);

            // 定位文件指针到文件头
            randomFile.seek(0);
            long startIndex = 0;
            int i = 1;
            // 获取文件内容的开始位置
            while ((n = randomFile.readByte()) != -1 && i <= 4) {
                if (n == '\n') {
                    startIndex = randomFile.getFilePointer();
                    i++;
                }
            }
            startIndex = startIndex - 1; // 这里一定要减1，因为前面多读了一个，这里很容易忽略
            // 获取文件内容结束位置
            randomFile.seek(randomFile.length());
            long endIndex = randomFile.getFilePointer();
            int j = 1;
            while (endIndex >= 0 && j <= 2) {
                endIndex--;
                randomFile.seek(endIndex);
                if (randomFile.readByte() == '\n') {
                    j++;
                }
            }

            // 设置保存上传文件的路径
            String realPath = "/src/main/files";
            File fileupload = new File(realPath);
            if (!fileupload.exists()) {
                fileupload.mkdir();
            }
            File saveFile = new File(realPath, filename);
            RandomAccessFile randomAccessFile = new RandomAccessFile(saveFile, "rw");
            // 根据起止位置从临时文件中读取文件内容
            randomFile.seek(startIndex);
            while (startIndex < endIndex) {
                randomAccessFile.write(randomFile.readByte());
                startIndex = randomFile.getFilePointer();
            }
            // 关闭输入输出流并 删除临时文件
            randomAccessFile.close();
            randomFile.close();
            tempFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
