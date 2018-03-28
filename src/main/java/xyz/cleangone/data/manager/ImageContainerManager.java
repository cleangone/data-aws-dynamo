package xyz.cleangone.data.manager;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import java.util.List;

public interface ImageContainerManager
{
    List<S3Link> getImages();
    List<String> getImageUrls();
    ImageManager getImageManager();

    S3Link createS3Link(String filePath);
    void addImage(S3Link image);
    void deleteImage(S3Link image);

    String getPrimaryUrl();
    void setPrimaryUrl(String bannerUrl);

    void save();
}
