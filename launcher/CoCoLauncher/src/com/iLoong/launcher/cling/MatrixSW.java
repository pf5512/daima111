package com.iLoong.launcher.cling;



import android.opengl.Matrix;

public class MatrixSW extends Matrix
{
	 /**
     * Rotates matrix m by angle a (in degrees) around the axis (x, y, z)
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param a angle to rotate in degrees
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    public static void rotateM(float[] rm, int rmOffset, float[] m, int mOffset,
            float a, float x, float y, float z) 
	{
        float[] r = new float[16];
        setRotateM(r, 0, a, x, y, z);
        multiplyMM(rm, rmOffset,  r, 0, m, mOffset);
    }

    /**
     * Rotates matrix m in place by angle a (in degrees)
     * around the axis (x, y, z)
     * @param m source matrix
     * @param mOffset index into m where the matrix starts
     * @param a angle to rotate in degrees
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    public static void rotateM(float[] m, int mOffset, float a, float x, float y, float z) 
    {
        float[] temp = new float[32];
        setRotateM(temp, 0, a, x, y, z);
        multiplyMM(temp, 16, temp, 0, m, mOffset);
        System.arraycopy(temp, 16, m, mOffset, 16);
    }
    
    public static void moveM(float[] m, int mOffset, float dx, float dy, float dz) 
    {
    	m[mOffset + 0] += dx;
    	m[mOffset + 1] += dy;
    	m[mOffset + 2] += dz;
    	m[mOffset + 3] += 0;
    	m[mOffset + 4] += dx;
    	m[mOffset + 5] += dy;
    	m[mOffset + 6] += dz;
    	m[mOffset + 7] += 0;
    	m[mOffset + 8] += dx;
    	m[mOffset + 9] += dy;
    	m[mOffset + 10] += dz;
    	m[mOffset + 11] += 0;
    	m[mOffset + 12] += dx;
    	m[mOffset + 13] += dy;
    	m[mOffset + 14] += dz;
    	m[mOffset + 15] += 0;
    }
    
    public static void moveV(float[] m, int mOffset, float dx, float dy, float dz) 
    {
    	m[mOffset + 0] += dx;
    	m[mOffset + 1] += dy;
    	m[mOffset + 2] += dz;
    	m[mOffset + 3] += 0;
    }
    
    /* Rotate a 3*4 matrix */
    /* m : 3*4 matrix */
    public static void rotate3M(float[] rm, int rmOffset, float[] m3, int mOffset,
            float a, float x, float y, float z) 
	{
    	float[] m4 = new float[32];
    	m4[0] = m3[mOffset+0];
    	m4[1] = m3[mOffset+1];
    	m4[2] = m3[mOffset+2];
    	m4[3] = 1;
    	m4[4] = m3[mOffset+3];
    	m4[5] = m3[mOffset+4];
    	m4[6] = m3[mOffset+5];
    	m4[7] = 1;
    	m4[8] = m3[mOffset+6];
    	m4[9] = m3[mOffset+7];
    	m4[10] = m3[mOffset+8];
    	m4[11] = 1;
    	m4[12] = m3[mOffset+9];
    	m4[13] = m3[mOffset+10];
    	m4[14] = m3[mOffset+11];
    	m4[15] = 1;   	
    	
        float[] r = new float[16];
        setRotateM(r, 0, a, x, y, z);
        multiplyMM(m4, 16,  r, 0, m4, 0);
        
    	rm[rmOffset+0] = m4[16+0];
    	rm[rmOffset+1] = m4[16+1];
    	rm[rmOffset+2] = m4[16+2];
    	rm[rmOffset+3] = m4[16+4];
    	rm[rmOffset+4] = m4[16+5];
    	rm[rmOffset+5] = m4[16+6];
    	rm[rmOffset+6] = m4[16+8];
    	rm[rmOffset+7] = m4[16+9];
    	rm[rmOffset+8] = m4[16+10];
    	rm[rmOffset+9] = m4[16+12];
    	rm[rmOffset+10] = m4[16+13];
    	rm[rmOffset+11] = m4[16+14];
    }
    
    public static void rotateV(float[] rv, int rvOffset, float[] v, int vOffset,
    		float a, float x, float y, float z) 
	{
        float[] r = new float[16];
        setRotateM(r, 0, a, x, y, z);
        multiplyMV(rv, rvOffset,  r, 0, v, vOffset);
    }
    
    public static void rotateV(float[] v, int vOffset, float a, float x, float y, float z) 
    {
        float[] temp = new float[20];
        setRotateM(temp, 0, a, x, y, z);
        multiplyMV(temp, 16, temp, 0, v, vOffset);
        System.arraycopy(temp, 16, v, vOffset, 4);
    }
}
