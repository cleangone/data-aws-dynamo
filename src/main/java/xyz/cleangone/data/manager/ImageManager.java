package xyz.cleangone.data.manager;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import xyz.cleangone.data.aws.AwsClientFactory;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;


public class ImageManager
{
    private final ImageContainerManager icMgr;
    private final AmazonS3 amazonS3Client;

    public ImageManager(ImageContainerManager icMgr)
    {
        this.icMgr = requireNonNull(icMgr);

        AwsClientFactory awsClientFactory = AwsClientFactory.getInstance();
        amazonS3Client = awsClientFactory.createS3Client();
    }

    public S3Link uploadImage(String filename, File file)
    {
        // store link in db
        S3Link s3Link = icMgr.createS3Link(filename);
        icMgr.addImage(s3Link);
        icMgr.save();

        // upload to s3
        s3Link.uploadFrom(file);
        s3Link.setAcl(CannedAccessControlList.PublicRead);

        return s3Link;
    }

    public void deleteImage(S3Link image)
    {
        try
        {
            // delete from s3
            amazonS3Client.deleteObject(new DeleteObjectRequest(image.getBucketName(), image.getKey()));

            // remove link from db
            icMgr.deleteImage(image);
            icMgr.save();
        }
        catch (Exception e)
        {
            int i=1;
        }
    }

    public List<String> getUrls()
    {
        List<S3Link> images = icMgr.getImages();
        if (images == null) { return new ArrayList<String>(); }

        return images.stream()
            .map(AwsClientFactory::getPublicUrl)
            .collect(Collectors.toList());
    }

    public static String getUrl(S3Link image)
    {
        return AwsClientFactory.getPublicUrl(image);
    }
    public static String getGradientUrl()
    {
        return getBaseUrl() + "banner/gradient.png";
    }
    public static String getBaseUrl()
    {
        return AwsClientFactory.getBaseImageUrl();
    }
}
