package com.changgou.file;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.file.util.FastDFSClient;
import com.changgou.file.util.FastDFSFile;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/file")
public class Controller {

    @PostMapping("/upload")
    public Result function(MultipartFile file){
        try {
            if(file == null){
                throw new RuntimeException("文件不存在");
            }
            String originalFilename = file.getOriginalFilename();
            if(StringUtils.isEmpty(originalFilename)){
                throw  new RuntimeException("文件不存在");
            }
            //扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //获取文件内容
            byte[] bytes = file.getBytes();
            //封装上传文件对象
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename,bytes,extName);

            String[] strings = FastDFSClient.upload(fastDFSFile);

            String url =   FastDFSClient.getTrackerUrl()+strings[0] + "/" + strings[1];

            return new Result(true, StatusCode.OK,"上传成功",url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,StatusCode.ERROR,"上传失败");
    }

}
