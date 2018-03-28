package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

import java.util.List;

public interface ImageContainer
{
    public List<S3Link> getImages();
    public void addImage(S3Link image);
}
