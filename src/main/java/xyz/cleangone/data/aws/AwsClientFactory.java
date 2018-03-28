package xyz.cleangone.data.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


import static xyz.cleangone.util.CleangoneEnv.*;
import static java.util.Objects.requireNonNull;

public class AwsClientFactory
{
    private static AwsClientFactory instance = null;

    private AWSCredentialsProvider credentialsProvider = null;
    private AmazonDynamoDBClientBuilder dynamoBuilder;
    private AmazonS3ClientBuilder s3Builder;

    public static AwsClientFactory getInstance()
    {
        if (instance == null) { instance = new AwsClientFactory(); }

        return instance;
    }

    private AwsClientFactory()
    {
        try { credentialsProvider = new DefaultAWSCredentialsProviderChain(); }
        catch (Exception e) { throw new AmazonClientException("Cannot load credentials from ProviderChain", e); }

        dynamoBuilder = AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .withCredentials(credentialsProvider);

        s3Builder = AmazonS3ClientBuilder.standard()
            .withRegion(REGION)
            .withCredentials(credentialsProvider);
    }

    // todo - massive hack
    public static String getPublicUrl(S3Link s3Link)
    {
        // ex. of bad url from s3Link
        // https://<bucket>.<region>.amazonaws.com/org/big/banner/squirrel.png

        // ex. of good url from s3 console
        // https://<region>.amazonaws.com/<bucket>/org/big/banner/squirrel.png

        String url = requireNonNull(s3Link).getUrl().toString();
        if (!url.contains(getRegion())) { throw new RuntimeException("s3Link " + url + " does not contain region " + getRegion()); }
        if (!url.contains(BUCKET_NAME)) { throw new RuntimeException("s3Link " + url + " does not contain bucket " + BUCKET_NAME); }

        String key = requireNonNull(s3Link).getKey();
        String newUrl = "https://s3-" + getRegion() + ".amazonaws.com/" + BUCKET_NAME + "/" + key;

        return newUrl;
    }

    public static String getRegion()
    {
        return REGION.getName();
    }

    public AmazonDynamoDB createDynamoClient()
    {
        return dynamoBuilder.build();
    }

    public AmazonS3 createS3Client()
    {
        return s3Builder.build();
    }

    public AWSCredentialsProvider getCredentialsProvider()
    {
        return credentialsProvider;
    }
}
