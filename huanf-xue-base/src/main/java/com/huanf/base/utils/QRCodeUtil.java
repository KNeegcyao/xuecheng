package com.huanf.base.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @description 二维码生成工具
 * @author Mr.M
 * @date 2022/10/3 0:03
 * @version 1.0
 */
public class QRCodeUtil {
 /**
  * 生成二维码
  *
  * @param content 二维码对应的URL
  * @param width   二维码图片宽度
  * @param height  二维码图片高度
  * @return
  */
 public String createQRCode(String content, int width, int height) throws IOException {
  if (StringUtils.isEmpty(content)) {
   throw new IllegalArgumentException("二维码内容不能为空");
  }

  ByteArrayOutputStream os = new ByteArrayOutputStream();
  String resultImage;

  // 二维码参数
  HashMap<EncodeHintType, Comparable> hints = new HashMap<>();
  hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 指定字符编码
  hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 纠错级别
  hints.put(EncodeHintType.MARGIN, 1); // 设置边距

  try {
   // 生成二维码
   QRCodeWriter writer = new QRCodeWriter();
   BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
   BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
   ImageIO.write(bufferedImage, "png", os); // 将图片写入输出流

   // 转码并加上 Base64 前缀
   resultImage = "data:image/png;base64," + EncryptUtil.encodeBase64(os.toByteArray());
  } catch (Exception e) {
   e.printStackTrace();
   throw new RuntimeException("生成二维码出错");
  } finally {
   os.close();
  }

  return resultImage;
 }

 public static void main(String[] args) throws IOException {
  QRCodeUtil qrCodeUtil = new QRCodeUtil();
  System.out.println(qrCodeUtil.createQRCode("https://54caaf863975deb742cf83bc42e95b11.loophole.site/orders/generatepaycode?payNo=https://4c6ec2a3931dca1964a1a00bfe26ddb6.loophole.site/orders/requestpay?payNo=1850511990396690432", 200, 200));
//  System.out.println(qrCodeUtil.createQRCode("https://4c6ec2a3931dca1964a1a00bfe26ddb6.loophole.site/orders/requestpay?payNo=1850511990396690432", 200, 200));

 }
}