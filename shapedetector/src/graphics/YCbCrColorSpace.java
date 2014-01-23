//http://robocup.bowdoin.edu/public/docs/tool/da/dc1/YCbCrImage_8java_source.html

package graphics;

import java.awt.Color;
import java.awt.color.ColorSpace;

public class YCbCrColorSpace extends ColorSpace {
	private static final long serialVersionUID = 1L;

	public YCbCrColorSpace() {
        super(TYPE_YCbCr, 3);
    }

    public float[] fromRGB(final float[] rgb) {
        float[] yCbCr = new float[3];

        yCbCr[0] = 16 + (float)(
                      65.481 * rgb[0] + 
                     128.553 * rgb[1] +
                      24.966 * rgb[2]
                   );
        yCbCr[1] = 128 + (float)(
                     -37.797 * rgb[0] + 
                     -74.203 * rgb[1] +
                      112.   * rgb[2]
                   );
        yCbCr[2] = 128 + (float)(
                     112.    * rgb[0] + 
                     -93.786 * rgb[1] +
                     -18.214 * rgb[2]
                   );

        for (int i=0; i<3; i++) {
            if (yCbCr[i] > 255)
                yCbCr[i] = 255;
            else if (yCbCr[i] < 0)
                yCbCr[i] = 0;
        }

        return yCbCr;
    }

    public int[] fromRGB(final int[] rgb) {
        int[] yCbCr = new int[3];
        fromRGB(rgb, yCbCr);
        return yCbCr;
    }

    public void fromRGB(final int[] rgb, final int[] yCbCr) {
        fromRGB(rgb[0], rgb[1], rgb[2], yCbCr);
    }

    public void fromRGB(final byte[] rgb, final byte[] yCbCr) {
        fromRGB(rgb[0], rgb[1], rgb[2], yCbCr);
    }
    
    public void fromRGB(byte[] rgb, int[] yCbCr) {
        fromRGB(rgb[0] & 0xff, rgb[1] & 0xff, rgb[2] & 0xff, yCbCr);
    }

    public void fromRGB(byte r, byte g, byte b, byte[] yCbCr) {
        int y =  16 + ((int) ( 65.738 * r +
                               129.057 * g +
                               25.064 * b  )) >> 8;
        int Cb = 128 + ((int) (-37.945 * r +
                               -74.494 * g +
                                112.439 * b  )) >> 8;
        int Cr = 128 + ((int) (112.439 * r +
                               -97.154 * g +
                              -18.285 * b  )) >> 8;

        
        if (y > 255)     yCbCr[0] = (byte)255;
        else if (y < 0)  yCbCr[0] = 0;
        else             yCbCr[0] = (byte)y;

        if (Cb > 255)    yCbCr[1] = (byte)255;
        else if (Cb < 0) yCbCr[1] = 0;
        else             yCbCr[1] = (byte)Cb;

        if (Cr > 255)    yCbCr[2] = (byte)255;
        else if (Cr < 0) yCbCr[2] = 0;
        else             yCbCr[2] = (byte)Cr;
    }

    public void fromRGB(int r, int g, int b, int[] yCbCr) {
        yCbCr[0] =  16 + ((int) ( 65.738 * r +
                                 129.057 * g +
                                  25.064 * b  )) >> 8;
        yCbCr[1] = 128 + ((int) (-37.945 * r +
                                 -74.494 * g +
                                 112.439 * b  )) >> 8;
        yCbCr[2] = 128 + ((int) (112.439 * r +
                                 -97.154 * g +
                                 -18.285 * b  )) >> 8;

        for (int i=0; i<3; i++) {
            if (yCbCr[i] > 255)
                yCbCr[i] = 255;
            else if (yCbCr[i] < 0)
                yCbCr[i] = 0;
        }
    }

    public float[] toRGB(float[] yCbCr) {
        float[] rgb = new float[3];
        float y = yCbCr[0] - 16;
        float Cb = yCbCr[1] - 128;
        float Cr = yCbCr[2] - 128;

        rgb[0] = (float)(
                   0.00456621 * y  + 
                   0.00625893 * Cr
                 );
        rgb[0] = (float)(
                    0.00456621 * y  + 
                   -0.00153632 * Cb +
                   -0.00318811 * Cr
                 );
        rgb[0] = (float)(
                   0.00456621 * y  + 
                   0.00791071 * Cb
                 );

        for (int i=0; i<3; i++) {
            if (rgb[i] > 1)
                rgb[i] = 1;
            else if (rgb[i] < 0)
                rgb[i] = 0;
        }

        return rgb;
    }


    public Color toRGB(int y, int Cb, int Cr) {
        int[] rgb = new int[3];
        toRGB(y, Cb, Cr, rgb);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public int[] toRGB(int[] yCbCr) {
        int[] rgb = new int[3];
        toRGB(yCbCr, rgb);
        return rgb;
    }

    public void toRGB(int[] yCbCr, int[] rgb) {
        toRGB(yCbCr[0], yCbCr[1], yCbCr[2], rgb);
    }

    public void toRGB(byte[] yCbCr, int[] rgb) {
        toRGB(yCbCr[0] & 0xff, yCbCr[1] & 0xff, yCbCr[2] & 0xff, rgb);
    }

    public void toRGB(int y, int Cb, int Cr, int[] rgb) {
        rgb[0] = ((int) ( 298.082 * (y - 16)   +
                          408.583 * (Cr - 128)    )) >> 8;
        rgb[1] = ((int) ( 298.082 * (y - 16)   +
                         -100.291 * (Cb - 128) +
                         -208.120 * (Cr - 128)    )) >> 8;
        rgb[2] = ((int) ( 298.082 * (y - 16)   +
                          516.411 * (Cb - 128)    )) >> 8;

        for (int i=0; i<3; i++) {
            if (rgb[i] > 255)
                rgb[i] = 255;
            else if (rgb[i] < 0)
                rgb[i] = 0;
        }
        
    }

    public float[] fromCIEXYZ(float[] cie_xyz) {
        float[] rgb = new float[3];

        rgb[0] = (float)(
                    3.240479 * cie_xyz[0] +
                   -1.537150 * cie_xyz[1] +
                   -0.498535 * cie_xyz[2]
                 );
        rgb[1] = (float)(
                   -0.969256 * cie_xyz[0] +
                    1.875992 * cie_xyz[1] +
                    0.041556 * cie_xyz[2]
                 );
        rgb[2] = (float)(
                    0.055648 * cie_xyz[0] +
                   -0.204043 * cie_xyz[1] +
                    1.057311 * cie_xyz[2]
                 );

        return fromRGB(rgb);
    }

    public float[] toCIEXYZ(float[] yCbCr) {
        float[] rgb = toRGB(yCbCr);
        float[] cie_xyz = new float[3];

        cie_xyz[0] = (float)(
                       0.412453 * rgb[0] +
                       0.357580 * rgb[1] +
                       0.180423 * rgb[2]
                     );
        cie_xyz[1] = (float)(
                       0.212671 * rgb[0] +
                       0.715160 * rgb[1] +
                       0.072169 * rgb[2]
                     );
        cie_xyz[2] = (float)(
                       0.019334 * rgb[0] +
                       0.119193 * rgb[1] +
                       0.950227 * rgb[2]
                     );

        return cie_xyz;
    }
}