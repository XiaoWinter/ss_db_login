package com.xiaoadong.ss_use_db.web_security.code;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component("imageProperty")
@Getter
@Setter
public class ImageProperty {

    private int fontSize = 40;// 验证码字体 （默认40）

    private int width = 250;// 图片验证码宽度 （默认250）

    private int height = 50;// 图片验证码高度（默认50）

    private int charAmt = 4;// 验证码字符数量 （默认4）

    private int expireIn = 60;//默认图片验证码保存在session中60秒

    private String inputImgName = "vcode";//表单中验证码的名称
}
