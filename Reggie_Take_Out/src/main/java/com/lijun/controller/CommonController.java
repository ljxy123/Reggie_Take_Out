package com.lijun.controller;

import com.lijun.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/*
* 文件的上传和下载
* */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.file.basePath}")
    private String basePath;

    /*
    * 上传文件
    * */
    @PostMapping("/upload")
    public Result<String> uploadImage(MultipartFile file) throws IOException {
        log.info("上传的文件:{}",file.toString());
        log.info("basePath:{}",basePath);

        File newFile = new File(basePath);
        //自动创建本地目录文件
        newFile.mkdirs();

        //重新组合文件名称
        String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String newName=uuid+substring;

        //将上传的图片上传到自定义本地文件当中
        file.transferTo(new File(newFile.getAbsoluteFile()+"/"+newName));

        return Result.success(newName);
    }

    /*
    * 下载本地中的文件
    * */

    @GetMapping("/download")
    public void downloadImage(String name, HttpServletResponse response) throws IOException {
        log.info("要回显的数据文件名为--->{}",name);

        //获取字节流
        FileInputStream fis = new FileInputStream(new File(basePath + "/" + name));
        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");

        //读取目标文件，并且写入浏览器
        int len;
        byte data[]=new byte[1024];
        while ((len=fis.read(data))!=-1){
            outputStream.write(data);
            outputStream.flush();
        }

        //关闭流
        outputStream.close();
        fis.close();
    }
}
