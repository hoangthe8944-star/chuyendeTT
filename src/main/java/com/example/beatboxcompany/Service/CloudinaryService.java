package com.example.beatboxcompany.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.beatboxcompany.Dto.UploadResultDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Spring tự động inject đối tượng Cloudinary đã cấu hình
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Tải file lên Cloudinary và trả về public ID cùng URL.
     * @param file File được gửi từ client (MultipartFile)
     * @param folder Tên thư mục lưu trữ trên Cloudinary (Ví dụ: "song_files", "album_covers")
     * @return UploadResultDto chứa publicId và secureUrl
     */
    public UploadResultDto uploadFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được rỗng.");
        }
        
        // 1. CHUẨN BỊ TÙY CHỌN CHO UPLOAD
        // Thiết lập thư mục và loại tài nguyên (image/video/raw)
        Map<String, Object> options = ObjectUtils.asMap(
            "resource_type", "auto", 
            "folder", folder
            // "public_id", "my_custom_id" // Nếu bạn muốn tự định nghĩa Public ID
        );

        try {
            // 2. GỌI API UPLOAD
            // file.getBytes() chính là DỮ LIỆU FILE cần tải lên
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(), 
                options
            );

            // 3. TRÍCH XUẤT KẾT QUẢ
            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");
            
            // 4. Trả về đối tượng DTO chứa thông tin cần thiết để lưu vào DB
            return new UploadResultDto(publicId, secureUrl);

        } catch (IOException e) {
            // Xử lý các lỗi có thể xảy ra trong quá trình upload
            throw new RuntimeException("Lỗi khi tải file lên Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa file khỏi Cloudinary bằng Public ID.
     * @param publicId ID công khai của file (được lưu trong DB của bạn)
     */
    public void deleteFile(String publicId) {
        try {
            // 1. GỌI API XÓA (destroy)
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Xử lý lỗi trong quá trình xóa
            throw new RuntimeException("Lỗi khi xóa file khỏi Cloudinary: " + e.getMessage(), e);
        }
    }
}