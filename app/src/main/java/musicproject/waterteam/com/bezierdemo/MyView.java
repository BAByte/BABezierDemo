package musicproject.waterteam.com.bezierdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by BA on 2018/3/26 0026.
 *
 * @Function : 用贝赛尔曲线实现圆的形变
 */

public class MyView extends View {
    private static final String TAG = "MyView";
    //起始位置
    private float bX=-350,bY=0;
    //用来变化的位置
    private float sX,sY;
    //结束位置
    private float eX=700,eY=0;
    //圆的半径
    private float r=80;
    //两个圆的距离
    private float distance=eX-sX;
    //动画进度
   // private float
    //贝塞尔画圆控制点的一个常量
    private float c= 0.551915024494f;
    //数据点
    private float[] dataPoint;
    //变化的数据点
    private float[] changeDP;
    //控制点
    private float[] ctrlPotint;
    //画笔
    private Paint paint;
    //颜色
    private int cricleColor=0xffff4081;
    //View的大小
    private float w,h;
    public MyView(Context context) {
        super(context);
    }

    //初始化位置画笔等
    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sX=bX;
        sY=bY;
        dataPoint=new float[8];
        changeDP=new float[8];
        ctrlPotint=new float[16];
        paint=new Paint();
        paint.setColor(cricleColor);

        initDataPoint(0,0);
        initCtrlPoint();
    }


    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w=w;
        this.h=h;
    }

    //初始化，数据点
    public void initDataPoint(float rAdd,float lAdd){
        //右
        dataPoint[0]=sX+r+rAdd;
        dataPoint[1]=sY;

        //上
        dataPoint[2]=sX;
        dataPoint[3]=sY+r;

        //左
        dataPoint[4]=sX-r+lAdd;
        dataPoint[5]=sY;

        //下
        dataPoint[6]=sX;
        dataPoint[7]=sY-r;
    }

    //初始化控制点
    public void initCtrlPoint(){
        ctrlPotint[0]=dataPoint[0];
        ctrlPotint[1]=dataPoint[1]+r*c;

        ctrlPotint[2]=dataPoint[2]+r*c;
        ctrlPotint[3]=dataPoint[3];

        ctrlPotint[4]=dataPoint[2]-r*c;
        ctrlPotint[5]=dataPoint[3];

        ctrlPotint[6]=dataPoint[4];
        ctrlPotint[7]=dataPoint[5]+r*c;

        ctrlPotint[8]=dataPoint[4];
        ctrlPotint[9]=dataPoint[5]-r*c;

        ctrlPotint[10]=dataPoint[6]-r*c;
        ctrlPotint[11]=dataPoint[7];

        ctrlPotint[12]=dataPoint[6]+r*c;
        ctrlPotint[13]=dataPoint[7];

        ctrlPotint[14]=dataPoint[0];
        ctrlPotint[15]=dataPoint[1]-r*c;
    }

    //用贝塞尔曲线画圆
    public void drawRaindrop(Canvas canvas){
        Path path=new Path();
        path.moveTo(dataPoint[0],dataPoint[1]);

        path.cubicTo(ctrlPotint[0],ctrlPotint[1],ctrlPotint[2],ctrlPotint[3],dataPoint[2],dataPoint[3]);
        path.cubicTo(ctrlPotint[4],ctrlPotint[5],ctrlPotint[6],ctrlPotint[7],dataPoint[4],dataPoint[5]);
        path.cubicTo(ctrlPotint[8],ctrlPotint[9],ctrlPotint[10],ctrlPotint[11],dataPoint[6],dataPoint[7]);
        path.cubicTo(ctrlPotint[12],ctrlPotint[13],ctrlPotint[14],ctrlPotint[15],dataPoint[0],dataPoint[1]);
        canvas.drawPath(path,paint);
    }

    /**
     * 开做动画
     * @author BA on 2018/3/27 0027
     * @param
     * @return
     * @exception
     */
    public void startAnim(){
        ValueAnimator animator= ValueAnimator.ofFloat(0,1);
        animator.setDuration(1500);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress=(float)animation.getAnimatedValue();
                sX=bX+progress*distance;
                changeXY(progress);
                Log.d(TAG, "onAnimationUpdate: "+progress);
            }
        });

        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startAnim();
        return super.onTouchEvent(event);
    }

    /**
     *  变化各个点的位置
     * @author BA on 2018/3/27 0027
     * @param
     * @return
     * @exception
     */
    public void changeXY(float progress){
        float rA,lA;
        if (progress<=0.25){
            rA=(r/2f)*(progress*4f);
            lA=0;
        }else if (progress<=0.5f){
            rA=r/2f;
            lA=-((r/2f)*(progress*4f-1));
        }else if (progress<=0.75f){
            rA=(r/2f)-(r/2f)*(progress*4f-2);
            lA=-r/2f;
        }else if (progress<=1){
            rA=0;
            lA=-(r/2f)+((r/2f)*(progress*4f-3));
        }else {
            lA=0;
            rA=(r*4)*(1-progress);
        }

        Log.d(TAG, "changeXY: "+rA+"::"+lA);

        initDataPoint(rA,lA);
        initCtrlPoint();
        invalidate();
    }

    /**
     * 画描边圆
     * @author BA on 2018/3/27 0027
     * @param
     * @return
     * @exception
     */
    public void drawCricle(Canvas canvas){
        Paint paint=new Paint();
        paint.setStrokeWidth(7);
        paint.setColor(cricleColor);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(bX,bY,r,paint);

        canvas.drawCircle(bX+distance,eY,r,paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.scale(1,-1);
        drawRaindrop(canvas);
        //drawCricle(canvas);
    }
}
