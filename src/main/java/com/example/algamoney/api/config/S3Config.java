package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@Configuration
public class S3Config {

    @Autowired
    private AlgamoneyApiProperty property;

    @Bean
    public AmazonS3 amazonS3() {

        AWSCredentials credentials = new BasicAWSCredentials(
                property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.SA_EAST_1)
                .build();

        String bucketName = property.getS3().getBucket();

        if (amazonS3.doesBucketExistV2(bucketName)) {
            System.out.println("✅ O bucket '" + bucketName + "' já existe.");
        } else {
            System.out.println("⏳ Criando o bucket '" + bucketName + "'...");
            amazonS3.createBucket(new CreateBucketRequest(bucketName));
            System.out.println("🎉 Bucket '" + bucketName + "' criado com sucesso!");

            BucketLifecycleConfiguration.Rule regraExpiracao =
                    new BucketLifecycleConfiguration.Rule()
                            .withId("Regra de expiração de arquivos temporários")
                            .withFilter(new LifecycleFilter(
                                    new LifecycleTagPredicate(new Tag("expirar", "true"))))
                            .withExpirationInDays(1)
                            .withStatus(BucketLifecycleConfiguration.ENABLED);

            BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
                    .withRules(regraExpiracao);

            amazonS3.setBucketLifecycleConfiguration(bucketName, configuration);
            System.out.println("🛠️ Configuração de expiração aplicada ao bucket '" + bucketName + "'.");
        }

        return amazonS3;
    }
}
