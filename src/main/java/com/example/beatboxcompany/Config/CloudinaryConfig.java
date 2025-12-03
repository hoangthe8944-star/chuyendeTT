package com.example.beatboxcompany.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    // Thay thế bằng thông tin tài khoản Cloudinary của bạn
    // (Nên lấy từ application.properties/yml)
    private static final String CLOUD_NAME = "YOUR_CLOUD_NAME";
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String API_SECRET = "YOUR_API_SECRET";

    @Bean
    public Cloudinary cloudinary() {
        // Khởi tạo đối tượng Cloudinary với thông tin xác thực
        Map<String, String> config = ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET
        );
        return new Cloudinary(config);
    }
}