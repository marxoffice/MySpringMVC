package service;

import annotation.Service;

import java.io.InputStream;

@Service
public interface FileUploadService {
    boolean uploadFile(InputStream fileSourceStream);
}
