package com.example.UniLabPass.service;

import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.httpClient.ModelClient;
import com.example.UniLabPass.utils.ByteArrayMultipartFile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ModelService {
    ModelClient modelClient;
    MyUserRepository myUserRepository;

    public Object verify(MultipartFile image1, String userId) throws IOException {
        MyUser myUser = myUserRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // Lấy dữ liệu từ URL và chuyển thành byte array
        URL url = new URL(myUser.getPhotoURL());
        byte[] fileBytes;
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            fileBytes = buffer.toByteArray();
        }

        // Chuyển thành MultipartFile
        MultipartFile image2 = new ByteArrayMultipartFile("file", "downloaded_file.jpg", "image/jpeg", fileBytes);


        return modelClient.verify(image1,image2);
    }

    public String healthcheck() {
        return modelClient.healthcheck();
    }
}
