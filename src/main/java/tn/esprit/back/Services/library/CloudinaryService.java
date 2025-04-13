package tn.esprit.back.Services.library;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Uploads a file to Cloudinary under the specified folder.
     * @param file the multipart file coming from a controller
     * @param folderName the target folder name in your Cloudinary account
     * @return secure URL of the uploaded file or null if the upload fails
     */
    public String uploadFile(MultipartFile file, String folderName) {
        try {
            // Define options, for example to specify a folder
            Map<?, ?> options = ObjectUtils.asMap("folder", folderName);
            // Upload file; the uploaded result is returned as a Map
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            e.printStackTrace(); // or use proper logging
            return null;
        }
    }
}