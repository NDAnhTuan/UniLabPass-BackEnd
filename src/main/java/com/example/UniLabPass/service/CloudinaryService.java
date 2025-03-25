package com.example.UniLabPass.service;

import com.cloudinary.Cloudinary;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;
    MyUserRepository myUserRepository;
    LabMemberRepository labMemberRepository;
    LogRepository logRepository;
    EventLogRepository eventLogRepository;

    // Update ảnh (ghi đè lên ảnh cũ bằng publicId)
    public CloudinaryResponse uploadFileMyUser(String userId, String labId, MultipartFile file) throws IOException {
        MyUser myUser =  myUserRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        try {
            // Upload ảnh lên Cloudinary
            var imageObj = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", myUser.getId(), "overwrite", true));

            // Lưu photoURL vào MyUser
            myUser.setPhotoURL(imageObj.get("url").toString());
            myUserRepository.save(myUser);

            // Trả về thông tin upload thành công
            return CloudinaryResponse.builder()
                    .userId(myUser.getId())
                    .url(myUser.getPhotoURL())
                    .build();
        } catch (RuntimeException e) {
            if (!labId.isEmpty()) labMemberRepository.deleteById(new LabMemberKey(labId,myUser.getId()));
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    public CloudinaryResponse uploadFileLogEvent(String logId, MultipartFile file) throws IOException {
        var log = eventLogRepository.findById(logId).orElseThrow(() -> new AppException(ErrorCode.LOG_NOT_EXIST));

        try {
            // Upload ảnh lên Cloudinary
            var imageObj = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", log.getId(), "overwrite", true));

            // Lưu photoURL vào MyUser
            log.setPhotoURL(imageObj.get("url").toString());
            eventLogRepository.save(log);

            // Trả về thông tin upload thành công
            return CloudinaryResponse.builder()
                    .userId(log.getId())
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
                    .userId(log.getId())
                    .url(log.getPhotoURL())
                    .build();
        } catch (RuntimeException e) {
            logRepository.delete(log);
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    // Xóa ảnh bằng userId
    public void deleteFileUser(String userId) throws IOException {
        MyUser myUser = myUserRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        cloudinary.uploader().destroy(myUser.getId(), ObjectUtils.emptyMap());
        myUser.setPhotoURL("");
        myUserRepository.save(myUser);
    }
    public void deleteFileLog(String logId) throws IOException {
        LaboratoryLog laboratoryLog = logRepository.findById(logId).orElse(null);
        EventLog eventLog = eventLogRepository.findById(logId).orElse(null);

        cloudinary.uploader().destroy(laboratoryLog != null ? laboratoryLog.getId() : eventLog.getId(), ObjectUtils.emptyMap());
        if (laboratoryLog != null) {
            laboratoryLog.setPhotoURL("");
            logRepository.save(laboratoryLog);
        } else {
            eventLog.setPhotoURL("");
            eventLogRepository.save(eventLog);
        }
    }
}