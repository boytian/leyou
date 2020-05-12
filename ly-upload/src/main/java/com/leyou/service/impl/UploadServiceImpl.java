package com.leyou.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExcptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.service.UploadService;
import com.leyou.upload.config.OSSConfig;
import com.leyou.upload.config.OSSProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author: TianTian
 * @Date: 2020/4/23 18:30
 */
@Service
public class UploadServiceImpl implements UploadService {
    //引入OSSProperties
    @Resource
    private OSSProperties prop;
    //引入OSSConfig
    @Resource
    private OSS client;


    //定义可以接收的图片文件类型列表

    private static final List<String> typeList= Arrays.asList("image/png","image/jpeg","image/bmp");

    //保存图片,一般将图片保存到专门资源服务器,不会保存在项目内部,nginx就可以做静态资源服务器

    private static final String dirPath="F:\\nginx-1.12.2\\html\\upimg";//改为自己的地址

    private final String BASE_IMG_DOMAIN = "http://image.leyou.com/upimg/";



    public String upload(MultipartFile file) {

        //图片校验  1.图片大小校验(配置文件中配置)   2. 图片类型校验  3.图片内容校验

        String type = file.getContentType();

        //图片类型校验

        if (!typeList.contains(type)) {

            throw new LyException(ExcptionEnum.INVALID_FILE_TYPE);      //需要导入LyException

        }

        //图片内容校验

        //声明BufferImage对象,用于接收图片文件的文件流

        BufferedImage image=null;

        //读取流文件,赋值给bufferImage对象

        try {

            image = ImageIO.read(file.getInputStream());

        } catch (IOException e) {

            throw new LyException(ExcptionEnum.INVALID_FILE_TYPE);

//            e.printStackTrace();



        }

        //校验读取后的文件是否为空

        if (image==null) {

            throw new LyException(ExcptionEnum.INVALID_FILE_TYPE);

        }

        //若通过校验,则保存图片

        //创建保存图片的文件夹,地址为nginx目录下的html中,创建upimg文件夹

        File dir = new File(dirPath);

        //如果目录不存在,则创建此文件夹

        if (!dir.exists()) {

            dir.mkdir();

        }

        //获取原始文件名称

        String filename = file.getOriginalFilename();

        //构建新的文件名称,通过UUID构建不重复的文件名称

        String newFileName = UUID.randomUUID().toString() +filename.substring(filename.indexOf("."));

        //构建要保存的文件对象

        File destFile = new File(dir, newFileName);



        //将文件上传到服务器

        try {

            file.transferTo(destFile);

        } catch (IOException e) {

            throw new LyException(ExcptionEnum.FILE_UPLOAD_ERROR);

        }

        //返回图片url地址

        return "http://image.leyou.com/upimg/"+newFileName;

    }

    @Override
    public Map<String, Object> getSignature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
        }catch (Exception e){
            throw new LyException(ExcptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
