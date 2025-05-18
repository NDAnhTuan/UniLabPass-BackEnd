package com.example.UniLabPass.service;

import com.example.UniLabPass.compositekey.LabMemberKey;
import com.example.UniLabPass.dto.request.LogCreationRequest;
import com.example.UniLabPass.entity.LabMember;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.enums.LogType;
import com.example.UniLabPass.enums.RecordType;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.LabMemberRepository;
import com.example.UniLabPass.repository.MyUserRepository;
import com.example.UniLabPass.repository.httpClient.ModelClient;
import com.example.UniLabPass.utils.ByteArrayMultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ModelService {
    ModelClient modelClient;
    MyUserRepository myUserRepository;
    LabMemberRepository labMemberRepository;

    LogService logService;

    @NonFinal
    @Value("${app.Global.RemainVerify}")
    int RemainVerify;

    public Object verify(MultipartFile image1, String userId, String labId, String recordType) throws IOException {
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
        // Gọi model
        ResponseEntity<String> modelResponse = modelClient.verify(image1, image2);
        // Parse JSON động
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root;

        // Truy cập trường samePerson
        boolean samePerson = false;
        var error = "";
        String body = modelResponse.getBody();
        root = objectMapper.readTree(body);
        if (root.get("samePerson") != null)
            samePerson = root.get("samePerson").asBoolean();
        if (root.get("error") != null)
            error = root.get("error").toString();
        boolean isIllegal = false;

        if (error.equals("")) {
            LabMember labMember = labMemberRepository.findById(new LabMemberKey(labId,userId)).orElseThrow(
                    () -> new AppException(ErrorCode.MEMBER_NOT_EXISTED)
            );
            if (labMember.getRole().getName().equals("PREMANAGER"))
                throw new AppException(ErrorCode.PREMANAGER_ERROR);
            if (samePerson) {
                labMember.setRemainVerify(RemainVerify);
                labMember.setExpiryRemain(LocalDateTime.now());
            }
            //Trường hợp mặt không giống
            else {
                // Nếu chưa hết hạn
                if (labMember.getExpiryRemain().isAfter(LocalDateTime.now())) {
                    labMember.setRemainVerify(labMember.getRemainVerify() - 1);
                    if (labMember.getRemainVerify() == 0) {
                        isIllegal = true;
                        labMember.setRemainVerify(RemainVerify);
                        labMember.setExpiryRemain(LocalDateTime.now().plusMinutes(5));
                    }
                }
                // Đã hết hạn (nghĩa là đây là lần đầu)
                else {
                    labMember.setRemainVerify(RemainVerify);
                    labMember.setExpiryRemain(LocalDateTime.now().plusMinutes(5));
                    labMember.setRemainVerify(labMember.getRemainVerify() - 1);
                }
            }
            labMemberRepository.save(labMember);
            LogCreationRequest logCreationRequest = LogCreationRequest.builder()
                    .labId(labId)
                    .userId(userId)
                    .recordType(recordType.equals(RecordType.CHECKIN) ? RecordType.CHECKIN : RecordType.CHECKOUT)
                    .logType(isIllegal ? LogType.ILLEGAL : LogType.LEGAL)
                    .build();
            logService.addNewLog(logCreationRequest, image1);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("result", root);
        response.put("isIllegal", isIllegal);

        return response;
    }

    public String healthcheck() {
        return modelClient.healthcheck();
    }
}
