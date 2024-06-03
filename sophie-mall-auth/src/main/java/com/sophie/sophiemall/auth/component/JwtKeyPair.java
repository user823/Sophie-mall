package com.sophie.sophiemall.auth.component;

import com.sophie.sophiemall.auth.constant.KeyStoreConstant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;

@Component
public class JwtKeyPair implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClassPathResource resource = new ClassPathResource("jwt.jks");
        // 如果密钥库文件不存在则创建
        if (!resource.exists()) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 保存
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, KeyStoreConstant.KEYSTORE_PASSWORD.toCharArray());
            keyStore.setKeyEntry(KeyStoreConstant.KEY_ALIAS, keyPair.getPrivate(), KeyStoreConstant.KEYSTORE_PASSWORD.toCharArray(), new java.security.cert.Certificate[]{});
            keyStore.store(new java.io.FileOutputStream(resource.getFile()), KeyStoreConstant.KEYSTORE_PASSWORD.toCharArray());
        }
    }
}
