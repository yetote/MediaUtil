package com.yetote.mediautil.glsl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.yetote.mediautil.glsl.objects.YUVObj;
import com.yetote.mediautil.glsl.programs.YUVProgram;
import com.yetote.mediautil.glsl.utils.YUVHelper;
import com.yetote.mediautil.util.yuvutil.YUVUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static com.yetote.mediautil.glsl.utils.YUVHelper.U_TYPE;
import static com.yetote.mediautil.glsl.utils.YUVHelper.V_TYPE;
import static com.yetote.mediautil.glsl.utils.YUVHelper.Y_TYPE;

public class YUVRenderer implements GLSurfaceView.Renderer {
    private Context context;
    private YUVObj yuvObj;
    private YUVProgram program;
    private int textureY, textureU, textureV;
    private int w, h;
    private ByteBuffer yData, uData, vData;
    private ByteBuffer yNullData, uNullData, vNullData;
    private static final String TAG = "MyRenderer";
    private int count = 0;
    private int textures[] = new int[3];
    private YUVUtil.YUV_TYPE type;
    public static final int SHOW_Y = 0X0001;
    public static final int SHOW_U = 0X0010;
    public static final int SHOW_V = 0X0100;
    private int flag = 0;
    private int glWidth, glHeight;

    public YUVRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0, 0, 0, 0);
        yuvObj = new YUVObj();
        program = new YUVProgram(context);
        YUVHelper.loadTexture(textures);
        Log.e(TAG, "onSurfaceCreated: ");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        glWidth = width;
        glHeight = height;
        Log.e(TAG, "onSurfaceChanged: ");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        program.useProgram();
        if (yData != null) {
            if ((flag & SHOW_Y) != 0) {
                Log.e(TAG, "onDrawFrame: Y");
                program.setUniform(textures[0], yData, Y_TYPE);
            } else {
                program.setUniform(textures[0], yNullData, Y_TYPE);
            }
            if ((flag & SHOW_U) != 0) {
                Log.e(TAG, "onDrawFrame: U");
                program.setUniform(textures[1], uData, U_TYPE);
            } else {
                program.setUniform(textures[1], uNullData, U_TYPE);
            }
            if ((flag & SHOW_V) != 0) {
                Log.e(TAG, "onDrawFrame: V");
                program.setUniform(textures[2], vData, V_TYPE);
            } else {
                program.setUniform(textures[2], vNullData, V_TYPE);
            }
        }
        yuvObj.bindData(program);
        yuvObj.draw();
        Log.e(TAG, "onDrawFrame: ");
    }

    public void obtainYUVData(byte[] y, byte[] u, byte[] v) {
        yData.put(y, 0, y.length);
        uData.put(u, 0, u.length);
        vData.put(v, 0, v.length);
    }

    public void setShowFlag(int flag) {
        this.flag = flag;
    }

    public boolean prepare(int w, int h, YUVUtil.YUV_TYPE type) {
        int[] arr = YUVUtil.distributionYUV(w, h, type);
        if (arr[0] == -1) {
            return false;
        }

        if (program == null) {
            Log.e(TAG, "prepare: program未初始化");
            return false;
        }
        resetResolution(w, h);
        yData = ByteBuffer.allocate(arr[0]).order(ByteOrder.nativeOrder());
        uData = ByteBuffer.allocate(arr[1]).order(ByteOrder.nativeOrder());
        vData = ByteBuffer.allocate(arr[2]).order(ByteOrder.nativeOrder());

        yNullData = ByteBuffer.allocate(arr[0]).order(ByteOrder.nativeOrder());
        uNullData = ByteBuffer.allocate(arr[1]).order(ByteOrder.nativeOrder());
        vNullData = ByteBuffer.allocate(arr[2]).order(ByteOrder.nativeOrder());

        byte[] yNull = new byte[w * h];
        byte[] uNull = new byte[w * h / 4];
        byte[] vNull = new byte[w * h / 4];

        Arrays.fill(yNull, Byte.MIN_VALUE);
        Arrays.fill(uNull, Byte.MIN_VALUE);
        Arrays.fill(vNull, Byte.MIN_VALUE);

        yNullData.put(yNull);
        uNullData.put(uNull);
        vNullData.put(vNull);

        return true;
    }

    private void resetResolution(int w, int h) {

        float glWidthHeightRatio = (float) glWidth / (float) glHeight;
        float videoWidthHeightRatio = (float) w / (float) h;
        Log.e(TAG, "resetResolution: gl宽高比" + glWidthHeightRatio);
        Log.e(TAG, "resetResolution: 视频宽高比" + videoWidthHeightRatio);

        float changeH = videoWidthHeightRatio * glWidthHeightRatio;
        program.sexPixel(w, h);
        yuvObj.changeRatio(changeH);
    }

}
