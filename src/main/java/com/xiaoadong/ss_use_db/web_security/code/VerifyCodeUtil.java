package com.xiaoadong.ss_use_db.web_security.code;

import com.xiaoadong.ss_use_db.web_security.properties.ImageProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

@Component("imgGenUtil")
public class VerifyCodeUtil {

    @Autowired
    private ImageProperties imageProperty;

    // 产生随机颜色
    private Color getRandColor() {
        Color[] colors = {Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.ORANGE, Color.PINK, Color.YELLOW};
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }

    // 绘制干扰线
    private void drawLine(Graphics2D g) {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int x1 = random.nextInt(imageProperty.getWidth());
            int y1 = random.nextInt(imageProperty.getHeight());
            int x2 = random.nextInt(imageProperty.getWidth());
            int y2 = random.nextInt(imageProperty.getHeight());
            g.setColor(getRandColor());
            g.drawLine(x1, y1, x2, y2);
        }
    }

    // 获取验证码图片
    public BufferedImage getVeriCodeImg(String validateCode) {
        BufferedImage img = new BufferedImage(imageProperty.getWidth(), imageProperty.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageProperty.getWidth(), imageProperty.getHeight());
        drawLine(g);
        g.setColor(Color.RED);
        Font font = new Font("宋体", Font.BOLD, imageProperty.getFontSize());
        g.setFont(font);
        int x, y = 0;
        String vCode = StringUtils.trim(validateCode);
        if (vCode.length() != imageProperty.getCharAmt()){
            System.err.println("验证码字符串字符数量有误， 请重新设置 ：{" + validateCode + "}");
            throw new RuntimeException("验证码长度有误");
        }
        for (int i = 0; i < imageProperty.getCharAmt(); i++) {
            String key = String.valueOf(vCode.charAt(i));

            x = i*(imageProperty.getWidth()/imageProperty.getCharAmt());
            y = imageProperty.getHeight()/2;
            g.drawString(key, x, y);
        }
        return img;
    }
}
