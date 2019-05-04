package com.xiaoadong.ss_use_db.web_security.code;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class ValidateCodeController {

    public static final String SESSION_KEY="SESSION_KEY_IMAGE";

    @Autowired
    private VerifyCodeUtil imgGenUtil;

    @Autowired
    private ImageProperty imageProperty;

    @GetMapping("code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //得到图形验证码
        ImageCode imageCode = createImageCode(request);
        //写入session
        fillSession(request,SESSION_KEY,imageCode);
        // 设置返回内容
        response.setContentType("image/jpg");
        //图片写入响应
        ImageIO.write(imageCode.getImage(),"JPEG", response.getOutputStream());
    }

    private void fillSession(HttpServletRequest request, String sessionKey, Object obj) {
        request.getSession().setAttribute(sessionKey,obj);
    }

    private ImageCode createImageCode(HttpServletRequest request) {

        //创建对象
        String s = RandomStringUtils.randomAlphanumeric(imageProperty.getCharAmt());
        BufferedImage image = imgGenUtil.getVeriCodeImg(s);

        return new ImageCode(image,s,imageProperty.getExpireIn());
    }
}
