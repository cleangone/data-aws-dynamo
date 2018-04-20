package xyz.cleangone.data.aws.dynamo.entity.image;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

import java.util.List;

public interface ImageContainer
{
    List<S3Link> getImages();
    void addImage(S3Link image);
}
