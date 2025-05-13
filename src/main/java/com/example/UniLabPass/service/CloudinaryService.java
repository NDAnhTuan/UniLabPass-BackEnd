package com.example.UniLabPass.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.response.CloudinaryResponse;
import com.example.UniLabPass.entity.EventLog;
import com.example.UniLabPass.entity.LaboratoryLog;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.EventLogRepository;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.LogRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CloudinaryService {

    Cloudinary cloudinary;
    MyUserRepository myUserRepository;
    LabMemberRepository labMemberRepository;
    LogRepository logRepository;
    EventLogRepository eventLogRepository;

    // Update ảnh (ghi đè lên ảnh cũ bằng publicId)
    public CloudinaryResponse uploadFileMyUser(String userId, MultipartFile file) throws IOException {
        try {
            // Upload ảnh lên Cloudinary
            var imageObj = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", userId,
                            "transformation", new Transformation()
                                    .fetchFormat("auto")
                                    .quality("auto")
                                    .width(1024)   // ✅ Resize ảnh về 1024px
                                    .height(1024)  // ✅ Giới hạn tối đa 1024px
                                    .crop("limit"), // ✅ Giữ tỷ lệ ảnh,
                            "overwrite", true));
            // Trả về thông tin upload thành công
            return CloudinaryResponse.builder()
                    .id(imageObj.get("public_id").toString())
                    .url(imageObj.get("url").toString())
                    .build();
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    private CloudinaryResponse uploadFileLogEvent(String logId, MultipartFile file) throws IOException {
        var log = eventLogRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_EXIST));

        try {
            // Upload ảnh lên Cloudinary
            var imageObj = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", log.getId(), "overwrite", true));

            // Lưu photoURL vào MyUser
                log.setPhotoURL(imageObj.get("secure_url").toString());
            eventLogRepository.save(log);

            // Trả về thông tin upload thành công
            return CloudinaryResponse.builder()
                    .id(log.getId())
                    .url(log.getPhotoURL())
                    .build();
        } catch (RuntimeException e) {
            eventLogRepository.delete(log);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    public CloudinaryResponse uploadFileLog(String logId, MultipartFile file, String logType) throws IOException {

        if (logType.equals("Event")) return uploadFileLogEvent(logId, file);

        var log = logRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_EXIST));

        try {
            // Upload ảnh lên Cloudinary
            var imageObj = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", log.getId(), "overwrite", true));

            // Lưu photoURL vào MyUser
            log.setPhotoURL(imageObj.get("url").toString());
            logRepository.save(log);

            // Trả về thông tin upload thành công
            return CloudinaryResponse.builder()
                    .id(log.getId())
                    .url(log.getPhotoURL())
                    .build();
        } catch (RuntimeException e) {
            logRepository.delete(log);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    // Xóa ảnh bằng Id
    public void deleteFile(String id) throws IOException {
        cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }
}