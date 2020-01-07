package com.star.common.utils.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

public class QRCode1Utils {
	private static Logger logger = LoggerFactory.getLogger(QRCode1Utils.class);
	private static final int BLACK = 0xff000000;
	private static final int WHITE = 0xFFFFFFFF;
	
	/** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param imgPath 图片路径 
     */  
    public static void encoderQRCode(String content, String imgPath) {  
        encoderQRCode(content, imgPath, "png", 300, 2);  
    }  
      
    /** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param output 输出流 
     */  
    public static void encoderQRCode(String content, OutputStream output) {  
        encoderQRCode(content, output, "png", 300, 2);  
    }  
      
    /** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param imgPath 图片路径 
     * @param imgType 图片类型 
     */  
    public static void encoderQRCode(String content, String imgPath, String imgType) {  
        encoderQRCode(content, imgPath, imgType, 300, 2);  
    }  
      
    /** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param output 输出流 
     * @param imgType 图片类型 
     */  
    public static void encoderQRCode(String content, OutputStream output, String imgType) {  
        encoderQRCode(content, output, imgType, 300, 2);  
    }  
  
    /** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param imgPath 图片路径 
     * @param imgType 图片类型 
     * @param size 二维码尺寸 
     */  
    public static void encoderQRCode(String content, String imgPath, String imgType, int size, int margin) {  
        try {  
            BufferedImage bufImg = createQRCode(content, size, margin);  
            // 生成二维码QRCode图片  
            ImageIO.write(bufImg, imgType, new File(imgPath));  
        } catch (Exception e) {
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 生成二维码(QRCode)图片 
     * @param content 存储内容 
     * @param output 输出流 
     * @param imgType 图片类型 
     * @param size 二维码尺寸 
     */  
    public static void encoderQRCode(String content, OutputStream output, String imgType, int size, int margin) {  
        try {  
            BufferedImage bufImg = createQRCode(content, size, margin);
            // 生成二维码QRCode图片  
            ImageIO.write(bufImg, imgType, output);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 生成二维码(QRCode)图片的公共方法 
     * @param content 存储内容 
     * @param imgType 图片类型 
     * @param size 二维码尺寸 
     * @return 
     */  
    private static BufferedImage createQRCode(String content, int size, int margin) {  
        try {
        	Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        	hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        	hints.put(EncodeHintType.MARGIN, margin);
        	BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        	
        	int[] rec = bitMatrix.getEnclosingRectangle();  
        	int resWidth  = rec[2] + 1;  
        	int resHeight = rec[3] + 1;  
        	BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);  
        	resMatrix.clear();  
        	for (int i = 0; i < resWidth; i++) {  
        	    for (int j = 0; j < resHeight; j++) {  
        	        if (bitMatrix.get(i + rec[0], j + rec[1])) { 
        	             resMatrix.set(i, j); 
        	        } 
        	    }  
        	}  
        	
        	int width  = bitMatrix.getWidth();
        	int height = bitMatrix.getHeight();
        	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        	for (int x = 0; x < width; x++) {
	        	for (int y = 0; y < height; y++) {
	        		image.setRGB(x, y, bitMatrix.get(x, y) == true ? BLACK : WHITE);
	        	}
        	}
        	return image;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
      
    /** 
     * 解析二维码（QRCode） 
     * @param imgPath 图片路径 
     * @return 
     */  
    public static String decoderQRCode(String imgPath) {  
        File imageFile = new File(imgPath);  
        try {  
            LuminanceSource source = new BufferedImageLuminanceSource(ImageIO.read(imageFile));
            BinaryBitmap bitmap    = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result != null ? result.getText() : null;
        } catch (IOException e) {  
            logger.error("解析二维码出错，图片路径：" + imgPath, e);
        } catch (NotFoundException e) {
        	logger.error("解析二维码出错，图片路径：" + imgPath, e);
		}
        return null;
    }  
      
    /** 
     * 解析二维码（QRCode） 
     * @param input 输入流 
     * @return 
     */  
    public static String decoderQRCode(InputStream input) {  
        try {  
            LuminanceSource source = new BufferedImageLuminanceSource(ImageIO.read(input));
            BinaryBitmap bitmap    = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result != null ? result.getText() : null;
        } catch (IOException e) {  
        	logger.error("解析二维码出错", e); 
        } catch (NotFoundException dfe) {  
        	logger.error("解析二维码出错", dfe); 
        }  
        return null;  
    }  
  
    public static void main(String[] args) {  
        String imgPath = "G:/b.png";  
        String encoderContent = "http://www.weijuju.com";  
        QRCode1Utils.encoderQRCode(encoderContent, imgPath);  
        System.out.println("========encoder success");  
          
        String decoderContent = QRCode1Utils.decoderQRCode(imgPath);  
        System.out.println("解析结果如下：");  
        System.out.println(decoderContent);  
        System.out.println("========decoder success!!!");  
    }  
}
