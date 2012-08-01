package nl.benbrucker.statisticscalc;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawCurve extends SurfaceView implements SurfaceHolder.Callback	{
 
        private ArrayList<PointF> datapoints;
        private static SurfaceHolder _surfaceHolder;
        
        private final float OFFSET = 2;
    	private final float STEPSIZE = 0.25f;
    	
    	private Path myPath = new Path();
    	private Paint paint  = new Paint();
        
    	private final int AMOUNT_OF_STDEVS = 3;
        
        public DrawCurve (Context context) {
            super(context);
            getHolder().addCallback(this);
            DrawCurve._surfaceHolder = getHolder();

        }

        public DrawCurve (Context context, AttributeSet attrs) {
            super(context, attrs);
            getHolder().addCallback(this);
            DrawCurve._surfaceHolder = getHolder();
        }
        
        @Override
        public void onDraw(Canvas canvas) {
        	int w = canvas.getWidth();
        	int h = canvas.getHeight();
            float xConversion =  (float)w / (Calculator.MAX - Calculator.MIN);
            double yConversion = ((double)h / 0.29d);
            /*
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.id.tiled_bg);
            final BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);
            d.
            d.draw(canvas);
            
            Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.diagonal_line);
            canvas.drawBitmap(myBitmap, 0, 0, null);*/
        	if (datapoints == null)	
        		calculateCurveData(canvas);
        	myPath.reset();
            paint.setAntiAlias(true);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setColor(getResources().getColor(R.color.ApplicationBg));
            paint.setStyle(Style.FILL_AND_STROKE);
            canvas.drawRect(0f, 0f, w, h, paint);
            paint.setColor(Color.GREEN);
            if (Calculator.isRange())	{
            	canvas.drawRect((float)Calculator.getFirstValue() * xConversion, 
        				(float)h,  
        				(float)Calculator.getSecondValue() * xConversion, 
        				0, 
        				paint);
            }
            paint.setColor(Color.argb(255, 51, 181, 229));
            myPath.moveTo(0, (float)h-2f);
            for (int i = 0; i < datapoints.size()-1; i++)	{
            	int current = i;
            	drawCurveSegment(myPath, canvas, paint, datapoints.get(current), datapoints.get(current+1));
                myPath.moveTo(datapoints.get(current+1).x, datapoints.get(current+1).y);
            }
            for (int i = -AMOUNT_OF_STDEVS; i <= AMOUNT_OF_STDEVS; i++)	{
            	canvas.drawLine((float)(Calculator.MEAN + i * Calculator.STDEV)*xConversion, 
            					(float)h, 
            					(float)(Calculator.MEAN + i * Calculator.STDEV)*xConversion, 
            					h-(float)(((1d/Math.sqrt(2d*Math.PI * Calculator.STDEV)) * Math.pow(Math.E,(-1 * 
            							Math.pow((Calculator.MEAN + i * Calculator.STDEV)-Calculator.MEAN,2))/(2 * Math.pow(Calculator.STDEV,2))))*yConversion), 
            					paint);
            }
            paint.setColor(Color.RED);
            if (!Calculator.isRange())	
            	canvas.drawLine((float)Calculator.getCurrentValue() * xConversion, 
            				(float)h,  
            				(float)Calculator.getCurrentValue() * xConversion, 
            				0, 
            				paint);   
            
        }
        
        private void calculateCurveData(Canvas canvas)	{
        	datapoints = new ArrayList<PointF>();
        	int w = canvas.getWidth();
        	int h = canvas.getHeight();
            float xConversion =  (float)w / (Calculator.MAX - Calculator.MIN);
            double yConversion = ((double)h / 0.29d);
            
            for (float x = Calculator.MIN; x <= Calculator.MAX; x += STEPSIZE)	{
            	double y = (1d/Math.sqrt(2d*Math.PI * Calculator.STDEV)) * Math.pow(Math.E,(-1 * Math.pow(x-Calculator.MEAN,2))/(2 * Math.pow(Calculator.STDEV,2)));
            	datapoints.add(new PointF(x * xConversion,h - (float) (y*yConversion+(double)OFFSET)));
            }
        }
        
        private void drawCurveSegment(Path myPath, Canvas canvas, Paint paint, PointF mPointa, PointF mPointb) {
            myPath.quadTo(mPointa.x, mPointa.y, mPointb.x, mPointb.y);
            canvas.drawPath(myPath, paint);
        }
 
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        	_surfaceHolder = holder;
        	repaint(holder);
        }
 
        public void surfaceCreated(SurfaceHolder holder) {
        	_surfaceHolder = holder;
        	repaint(holder);
        }
 
        public void surfaceDestroyed(SurfaceHolder holder) {
        	_surfaceHolder = holder;
        	repaint(holder);
        }

		public void update() {
        	repaint(_surfaceHolder);
		}
		
		private void repaint(SurfaceHolder surface) {
			if (surface != null) {
				Canvas canvas = null;
				try {
					canvas = surface.lockCanvas();
					if (canvas != null) {
						synchronized (surface) {
							this.onDraw(canvas);
						}
					} else {
						Log.w(VIEW_LOG_TAG, "SurfaceHolder returned null Canvas");
					}
				} finally {
					if (canvas != null)
						surface.unlockCanvasAndPost(canvas);
				}
			}
		}
    }
