package service;

import java.io.InputStream;

public interface FileUploadService {
    boolean uploadFile(InputStream fileSourceStream);
}
