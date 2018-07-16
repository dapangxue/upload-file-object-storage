package com.cyber.storage.controller;

import com.cyber.storage.util.cos.CosInitConfigUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Create by IDEA
 *
 * @author wuxue
 * @date 18-7-13
 */
@Controller
@RequestMapping(value = "/bucket")
public class CosOperateController {

    @RequestMapping(value = "/create")
    public String createBucket(@RequestParam String bucketName, ModelMap modelMap) throws Exception {
        COSClient cosclient = CosInitConfigUtil.cosInitConfig();

        // 判断bucketName是否已经存在
        if (cosclient.doesBucketExist(bucketName)) {
            modelMap.addAttribute("createBucketMsg", "bucket已存在");
        } else {
            try {
                // 创建bucket
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                // 设置bucket的权限为PublicRead(公有读私有写), 其他可选有私有读写, 公有读私有写
                createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                Bucket bucket = cosclient.createBucket(createBucketRequest);

                modelMap.addAttribute("createBucketMsg", "创建bucket成功");
            } catch (Exception e) {
                e.printStackTrace();
                modelMap.addAttribute("createBucketMsg", "发生未知错误，请检查COS配置");
            } finally {
                // 关闭客户端
                cosclient.shutdown();
            }
        }
        return "index";
    }

    @RequestMapping(value = "/delete")
    public String deleteBucket(@RequestParam String bucketName, ModelMap modelMap) {
        COSClient cosclient = CosInitConfigUtil.cosInitConfig();

        // 判断bucket是否存在
        if (cosclient.doesBucketExist(bucketName)) {
            cosclient.deleteBucket(bucketName);
            modelMap.addAttribute("deleteBucketMsg", "bucket删除成功");
        } else {
            modelMap.addAttribute("deleteBucketMsg", "bucket不存在，无需删除");
        }
        return "index";
    }

    @RequestMapping(value = "/upload")
    public String uploadFileFromLocalToBucket(@RequestParam(value = "bucketName") String bucketName,
                                              @RequestParam(value = "file") MultipartFile file,
                                              ModelMap modelMap) {
        COSClient cosclient = CosInitConfigUtil.cosInitConfig();

        // 获取文件的原名
        String fileOriginName = file.getOriginalFilename();
        File virtualFile = new File(fileOriginName);

        try {
            FileOutputStream os = new FileOutputStream(virtualFile);
            os.write(file.getBytes());
            os.close();
            file.transferTo(virtualFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileOriginName, virtualFile);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            String etag = putObjectResult.getETag();
            String imgUrl = "http://" + bucketName + ".cossh.myqcloud.com/" + fileOriginName;
            modelMap.addAttribute("uploadObjectURL", imgUrl);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cosclient.shutdown();
        }
        return "index";
    }

    @RequestMapping(value = "/delete/object")
    public String deleteFileFromBucket(@RequestParam(value = "bucketName") String bucketName,
                                       @RequestParam(value = "deleteObjectName") String deleteObjectName) {
        COSClient cosclient = CosInitConfigUtil.cosInitConfig();

        try {
            cosclient.deleteObject(bucketName, deleteObjectName);
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } finally {
            cosclient.shutdown();
        }
        return "index";
    }
}
