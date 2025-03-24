package com.example.UniLabPass.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        // Parse CLOUDINARY_URL
        CloudinaryCredentials credentials = parseCloudinaryUrl(cloudinaryUrl);

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", credentials.getCloudName(),
                "api_key", credentials.getApiKey(),
                "api_secret", credentials.getApiSecret()
        ));
    }

    private CloudinaryCredentials parseCloudinaryUrl(String url) {
        Pattern pattern = Pattern.compile("cloudinary://(\\w+):(\\w+)@([\\w-]+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return new CloudinaryCredentials(matcher.group(3), matcher.group(1), matcher.group(2));
        }
        throw new IllegalArgumentException("Invalid CLOUDINARY_URL format");
    }

    private static class CloudinaryCredentials {
        private final String cloudName;
        private final String apiKey;
        private final String apiSecret;

        public CloudinaryCredentials(String cloudName, String apiKey, String apiSecret) {
            this.cloudName = cloudName;
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
        }

        public String getCloudName() { return cloudName; }
        public String getApiKey() { return apiKey; }
        public String getApiSecret() { return apiSecret; }
    }
}
