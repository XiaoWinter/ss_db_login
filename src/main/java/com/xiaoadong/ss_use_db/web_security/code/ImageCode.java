package com.xiaoadong.ss_use_db.web_security.code;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

@Getter
@Setter
public class ImageCode {
    private BufferedImage image;
    private String code;
    private LocalDateTime expireTime;

    /**
     * @param image 图片
     * @param code 验证码
     * @param expireIn 输入多少秒后过期
     */
    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
