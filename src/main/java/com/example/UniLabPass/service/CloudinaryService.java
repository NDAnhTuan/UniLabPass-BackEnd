package com.example.UniLabPass.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.UniLabPass.dto.response.CloudinaryResponse;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;
    MyUserRepository myUserRepository;

    // Update ảnh (ghi đè lên ảnh cũ bằng publicId)
    public CloudinaryResponse uploadFile(String userId, MultipartFile file) throws IOException {
        MyUser myUser =  myUserRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var imageOjb = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", myUser.getId(), "overwrite", true));

        myUser.setPhotoURL(imageOjb.get("url").toString());
        myUserRepository.save(myUser);
        return CloudinaryResponse.builder()
                .userId(imageOjb.get("public_id").toString())
                .url(imageOjb.get("url").toString())
                .build();
    }

    // Xóa ảnh bằng publicId
    public void deleteFile(String userId) throws IOException {
        MyUser myUser = myUserRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        cloudinary.uploader().destroy(myUser.getId(), ObjectUtils.emptyMap());
        myUser.setPhotoURL("");
        myUserRepository.save(myUser);
    }
}