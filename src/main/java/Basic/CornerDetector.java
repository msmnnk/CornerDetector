package Basic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * This file contains code of Gaussian filter from WhiteySage (GitHub)
 */

// Task 4b-c: implement corner detection algorithm
public class CornerDetector {
    // create a kernel
    public static double[][] createKernel(double sigma) {
        int W = 3;
        double[][] kernel = new double[W][W];
        double mean = W / 2;
        double sum = 0;
        for (int x = 0; x < W; ++x) {
            for (int y = 0; y < W; ++y) {
                // Gaussian kernel follows the Gaussian distribution formula
                kernel[x][y] = (Math.exp(-0.5 * (Math.pow((x - mean) / sigma, 2.0) + Math.pow((y - mean) / sigma, 2.0))) / (2 * Math.PI * sigma * sigma));
                sum += kernel[x][y];
            }
        }
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < W; j++) {
                kernel[i][j] /= sum;
            }
        }
        return kernel;
    }

    // Gaussian filter for X
    public static double[][] gaussianX(double sigma) {
        double[] filter = {-1, 0, 1};
        double[][] kernel = createKernel(sigma);
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                kernel[i][j] *= filter[j];
            }
        }
        return kernel;
    }

    // Gaussian filter for Y
    public static double[][] gaussianY(double sigma) {
        double[] filter = {-1, 0, 1};
        double[][] kernel = createKernel(sigma);
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                kernel[i][j] *= filter[i];
            }
        }
        return kernel;
    }

    // image convolution and derivatives with respect to x and y
    public static double[][] convolution(double[][] original, double[][] kernel) {
        int additional = kernel.length - 1;
        int semiAdditional = additional / 2;
        double[][] newMatrix = new double[original[0].length + additional][original.length + additional];
        // big matrix
        for (int x = 0, i = semiAdditional + x; x < original[0].length; x++, i++) {
            for (int y = 0, j = semiAdditional + y; y < original.length; y++, j++) {
                newMatrix[i][j] = original[y][x];
            }
        }
        double[][] result = new double[original.length][original[0].length];
        for (int x = 0, i = semiAdditional + x; x < original[0].length; x++, i++) {
            for (int y = 0, j = semiAdditional + y; y < original.length; y++, j++) {
                double[][] sub = subMatrix(i - semiAdditional, j - semiAdditional, i + semiAdditional, j + semiAdditional, newMatrix);
                result[y][x] = convolutionElements(sub, kernel);
            }
        }
        return result;
    }

    // construct the structure tensor
    public static double[][] subMatrix(int x0, int y0, int xN, int yN, double[][] original) {
        double[][] result = new double[xN - x0 + 1][yN - y0 + 1];
        for (int x = 0, i = x0 + x; x < result[0].length; x++, i++) {
            for (int y = 0, j = y0 + y; y < result.length; y++, j++) {
                result[x][y] = original[i][j];
            }
        }
        return result;
    }

    // find the elements of convolution
    public static int convolutionElements(double[][] matrix, double[][] kernel) {
        int result = 0;
        for (int i = 0; i < kernel[0].length; i++) {
            for (int j = 0; j < kernel.length; j++) {
                result += matrix[i][j] * kernel[i][j];
            }
        }
        return result;
    }

    // convert image to an array
    static double[][] transformImageToArray(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        double[][] image = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = new Color(bufferedImage.getRGB(j, i));
                image[i][j] = color.getRed();
            }
        }
        return image;
    }

    // normalize the matrix
    public static void normalize(double[][] matrix) {
        double maxR = 0;
        for (double[] doubles : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (maxR < doubles[j]) {
                    maxR = doubles[j];
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] /= maxR;
            }
        }
    }

    // find R by applying two Gaussian kernels of different sigmas on the image
    public static double[][] findR(BufferedImage image, double k, boolean isHarris) throws IOException {
        // make the image grey
        greyImage(image);
        // Gx
        double[][] kernelX = CornerDetector.gaussianX(0.5);
        // Gy
        double[][] kernelY = CornerDetector.gaussianY(0.5);
        // Ix
        double[][] I_x = CornerDetector.convolution(CornerDetector.transformImageToArray(image), kernelY);
        // Iy
        double[][] I_y = CornerDetector.convolution(CornerDetector.transformImageToArray(image), kernelX);
        // Ix^2
        double[][] I_x2 = new double[I_x.length][I_x[0].length];
        for (int i = 0; i < I_x.length; i++) {
            for (int j = 0; j < I_x[0].length; j++) {
                I_x2[i][j] = I_x[i][j] * I_x[i][j];
            }
        }
        // Iy^2
        double[][] I_y2 = new double[I_y.length][I_y[0].length];
        for (int i = 0; i < I_x.length; i++) {
            for (int j = 0; j < I_x[0].length; j++) {
                I_y2[i][j] = I_y[i][j] * I_y[i][j];
            }
        }
        // Ixy
        double[][] I_xy = new double[I_y.length][I_y[0].length];
        for (int i = 0; i < I_x.length; i++) {
            for (int j = 0; j < I_x[0].length; j++) {
                I_xy[i][j] = I_x[i][j] * I_y[i][j];
            }
        }
        // filter with a different sigma value
        double[][] kernel = CornerDetector.createKernel(1);
        // Sx^2
        double[][] S_x2 = CornerDetector.convolution(I_x2, kernel);
        // Sy^2
        double[][] S_y2 = CornerDetector.convolution(I_y2, kernel);
        // Sxy
        double[][] S_xy = CornerDetector.convolution(I_xy, kernel);
        // create R matrix
        double[][] R = new double[image.getHeight()][image.getWidth()];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                // detector type separator (true = Harris, false = Shi-Tomasi)
                if (isHarris) {
                    R[i][j] = responseHarris(S_x2[i][j], S_y2[i][j], S_xy[i][j], k);
                } else {
                    R[i][j] = responseShiTomasi(S_x2[i][j], S_y2[i][j]);
                }
            }
        }
        return R;
    }

    // Task 4b: Harris response function: R = λ1λ2 − 𝛼(λ1 + λ2)^2
    public static double responseHarris(double lambda1, double lambda2, double S_xy, double k) {
        double trace = lambda1 + lambda2;
        double det = lambda1 * lambda2 - S_xy * S_xy;
        return det - k * trace * trace;
    }

    // Task 4c: Shi-Tomasi response function: R = minimum(λ1, λ2)
    public static double responseShiTomasi(double lambda1, double lambda2) {
        return Math.min(lambda1, lambda2);
    }

    // convert to 0-1 range
    public static void threshold(double[][] R) {
        double thresh = 0.001;
        for (int i = 0; i < R.length; i++) {
            for (int j = 0; j < R[0].length; j++) {
                if (R[i][j] < thresh) {
                    R[i][j] = 0;
                }
            }
        }
    }

    // to pick up the optimal values to indicate corners,
    // find the local maxima as corners within the area 5 by 5
    public static BufferedImage cornerDetector(double[][] R, BufferedImage image) {
        double max;
        int Y;
        int X;
        for (int i = 2; i < R.length - 2; i++) {
            for (int j = 2; j < R[0].length - 2; j++) {
                max = 0;
                Y = 0;
                X = 0;
                if (R[i][j] > 0) {
                    for (int y = i - 2; y < i + 3; y++) {
                        for (int x = j - 2; x < j + 3; x++) {
                            if (max < R[y][x]) {
                                max = R[y][x];
                                Y = y;
                                X = x;
                                R[y][x] = 0;
                            } else {
                                R[y][x] = 0;
                            }
                        }
                    }
                    // set the white color for corners drawing
                    R[Y][X] = 1;
                    Color c = new Color(255, 255, 255, 255);
                    Graphics2D g2D = image.createGraphics();
                    g2D.setColor(c);
                    g2D.fillRect(X-1,Y-1,3,3);
                    image.setRGB(X, Y, c.getRGB());
                }
            }
        }
        return image;
    }

    // convert image to grey
    public static void greyImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color c = new Color(image.getRGB(i, j));
                // RGB values of the grey scale pixel
                int red = (int) (c.getRed() * 0.2126);
                int green = (int) (c.getGreen() * 0.7152);
                int blue = (int) (c.getBlue() * 0.0722);
                Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                image.setRGB(i, j, newColor.getRGB());
            }
        }
    }

}
