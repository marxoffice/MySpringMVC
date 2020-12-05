package service;

import java.io.InputStream;

public interface FileUploadService {
    void uploadFile(InputStream fileSourceStream);
}
