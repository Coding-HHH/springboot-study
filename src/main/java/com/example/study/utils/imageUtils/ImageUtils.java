package com.example.study.utils.imageUtils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 图片处理工具类: 对图片进行拼接（横向或纵向）
 */
@Slf4j
public class ImageUtils {
    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\Administrator\\Desktop\\";
        String path1 = filePath + "a.jpg";
        String path2 = filePath + "b.jpg";
        String path3 = filePath + "reduceImg.jpg";
        int type = 2;
        File file1 = new File(path1);
        if (type == 1){
            //等比例缩小图片的高度
            int height = ImageIO.read(file1).getHeight();
            reduceHeightImg(path2,path3,height);
        }else if (type == 2){
            //等比例缩小图片的宽度
            int width = ImageIO.read(file1).getWidth();
            reduceWidthImg(path2,path3,width);
        }
        //纵向拼接 拼接图片
        mergeImage(path1, path3,  type, filePath+"c.jpg");
    }

    /**
     * 图片拼接
     * @param path1     图片1路径
     * @param path2     图片2路径
     * @param type      1 横向拼接， 2 纵向拼接
     * （注意：必须两张图片长宽一致）
     */
    public static void mergeImage( String path1, String path2, int type, String targetFile) throws IOException {
        File file1 = new File(path1);
        File file2 = new File(path2);
        //两张图片的拼接
        int len = 2;
        BufferedImage[] images = new BufferedImage[len];
        images[0] = ImageIO.read(file1);
        images[1] = ImageIO.read(file2);
        int[][] ImageArrays = new int[len][];

        for (int i = 0; i < len; i++) {
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return;
        }
        if (type == 2 && newHeight < 1) {
            return;
        }
        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
            //输出想要的图片
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));
            log.info("拼接图片成功！");

        } catch (Exception e) {
            log.error("拼接图片失败:{}", e);
        }
    }


    /**
     * 等比率缩放
     * @param imgsrc  原图片路径
     * @param imgdist  缩放图片路径
     * @param widthdist 指定缩放宽度
     */
    public static void reduceWidthImg(String imgsrc, String imgdist, int widthdist) {
        try {
            File srcfile = new File(imgsrc);
            if (!srcfile.exists()) {
                return;
            }
            //获取图片对象
            Image src = ImageIO.read(srcfile);
            //获取缩放比率因子，保留2位小数
            double  factor = new BigDecimal((float)widthdist/src.getWidth(null)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //获取缩放后的高度
            double  height = src.getHeight(null)*factor;
            //缓存图片对象
            BufferedImage tag= new BufferedImage((int) widthdist, (int) height,
                    BufferedImage.TYPE_INT_RGB);
            //绘制图片
            tag.getGraphics().drawImage(src.getScaledInstance(widthdist, (int) height,  Image.SCALE_SMOOTH), 0, 0, null);
            //获取输入流对象
            FileOutputStream out = new FileOutputStream(imgdist);
            //图片编码器
            ImageIO.write(tag, "jpg", new File(imgdist));
            log.info("缩放图片宽度成功！");
        } catch (IOException ex) {
            log.error("缩放图片宽度失败:{}", ex);
        }
    }


    /**
     * 等比率缩放
     * @param imgsrc  原图片路径
     * @param imgdist  缩放图片路径
     * @param heightdist 指定缩放高度
     */
    public static void reduceHeightImg(String imgsrc, String imgdist, int heightdist) {
        try {
            File srcfile = new File(imgsrc);
            if (!srcfile.exists()) {
                return;
            }
            //获取图片对象
            Image src = javax.imageio.ImageIO.read(srcfile);
            //获取缩放比率因子，保留2位小数
            double  factor = new BigDecimal((float)heightdist/src.getHeight(null)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //获取缩放后的高度
            double  width = src.getWidth(null)*factor;
            //缓存图片对象
            BufferedImage tag= new BufferedImage((int) width, heightdist,
                    BufferedImage.TYPE_INT_RGB);
            //绘制图片
            tag.getGraphics().drawImage(src.getScaledInstance((int)width, heightdist,  Image.SCALE_SMOOTH), 0, 0, null);
            //获取输入流对象
            FileOutputStream out = new FileOutputStream(imgdist);
            //图片编码器
            ImageIO.write(tag, "jpg", new File(imgdist));
            log.info("缩放图片高度成功！");
        } catch (IOException ex) {
            log.error("缩放图片高度失败:{}", ex);
        }
    }

}
