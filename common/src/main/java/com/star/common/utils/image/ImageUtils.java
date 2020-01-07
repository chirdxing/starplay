package com.star.common.utils.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片工具类库
 * @date 2020年1月7日
 * @version 1.0
 */
public class ImageUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * 将网络图片进行Base64位编码
     * @param imageUrl 图片的url路径，如http://.....xx.png
     * @return
     */
    public static String encodeImageToBase64(URL imageUrl) {
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (Exception e) {
            LOGGER.error("编码异常, imageUrl=" + imageUrl, e);
        }
        return Base64.encodeBase64String(outputStream.toByteArray());
    }
    
    /**
     * 将网络图片进行Base64位编码
     * @param url 图片的url路径，如http://.....xx.png
     * @return
     */
    public static String encodeImgageToBase64(String url) {
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL(url));
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
        }catch (Exception e) {
            LOGGER.error("编码异常,url=" + url, e);
        }
        return Base64.encodeBase64String(outputStream.toByteArray());
    }
    
    
    /**
     * 将本地图片进行Base64位编码
     * @param imageFile
     * @return
     */
    public static String encodeImgageToBase64(File imageFile) {
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (IOException e) {
            LOGGER.error("编码异常,imageFile="+imageFile,e);
        }
        return Base64.encodeBase64String(outputStream.toByteArray());
    }

    /**
     * 将Base64位编码的图片进行解码，并保存到指定目录
     * @param base64
     *			base64编码的图片信息
     * @return
     */
    public static void decodeBase64ToImage(String base64, String path,
                                           String imgName) {
        try {
            File pathFile = new File(path);
            if(!pathFile.exists()){
                pathFile.mkdirs();
            }
            FileOutputStream write = new FileOutputStream(new File(path
                    + imgName));
            byte[] decoderBytes = Base64.decodeBase64(base64);
            write.write(decoderBytes);
            write.close();
        } catch (IOException e) {
            LOGGER.error("编码异常,base64="+base64,e);
        }
    }
    
}
