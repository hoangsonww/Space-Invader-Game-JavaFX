package com.example.spaceshootergamejavafx;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessor {

    public static void main(String[] args) {
        try {
            // Đọc ảnh vào BufferedImage: boss and enemy
            BufferedImage enemyimage = ImageIO.read(new File("src/main/resources/enemy.png"));
            BufferedImage bossimage = ImageIO.read(new File("src/main/resources/boss.png"));
            BufferedImage powerupimage = ImageIO.read(new File("src/main/resources/powerup.png"));

            // Cắt nền trắng
            BufferedImage resultImage = removeWhiteBackground(enemyimage);
            BufferedImage bossresultImage = removeWhiteBackground(bossimage);
            BufferedImage powerupresultImage = removeWhiteBackground(powerupimage);

            // Lưu ảnh đã xử lý
            ImageIO.write(resultImage, "png", new File("src/main/resources/enemy.png"));
            ImageIO.write(bossresultImage, "png", new File("src/main/resources/boss.png"));
            ImageIO.write(powerupresultImage, "png", new File("src/main/resources/powerup.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hàm cắt nền trắng khỏi ảnh, giữ lại vật thể
     * @param image BufferedImage gốc
     * @return BufferedImage mới với nền trắng bị cắt
     */
    public static BufferedImage removeWhiteBackground(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int tolerance = 20; // Độ sai lệch cho phép với màu trắng (255)

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y), true);

                // Nếu pixel gần màu trắng, chuyển thành trong suốt
                if (isNearWhite(color, tolerance)) {
                    newImage.setRGB(x, y, new Color(0, 0, 0, 0).getRGB()); // Pixel trong suốt
                } else {
                    newImage.setRGB(x, y, color.getRGB()); // Giữ nguyên vật thể
                }
            }
        }

        return newImage;
    }

    // Kiểm tra pixel có gần trắng không
    private static boolean isNearWhite(Color color, int tolerance) {
        return color.getRed() > 255 - tolerance &&
                color.getGreen() > 255 - tolerance &&
                color.getBlue() > 255 - tolerance;
    }

}
