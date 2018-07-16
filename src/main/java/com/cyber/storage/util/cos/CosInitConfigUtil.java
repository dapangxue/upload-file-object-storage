package com.cyber.storage.util.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;

/**
 * Create by IDEA
 *
 * @author wuxue
 * @date 18-7-13
 */
public class CosInitConfigUtil {
    public static COSClient cosInitConfig() {
        // 1 初始化用户身份信息(secretId, secretKey)
        // 在这里请使用你自己的accessKey和secretKey
        COSCredentials cred = new BasicCOSCredentials("AKIxxxxxxxxxxxx", "Vo6xxxxxxxxxxxxxxxxxxxxxxx");
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        return cosclient;
    }
}
