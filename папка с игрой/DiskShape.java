package com.example.hanoitower;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

class DiskShape extends ShapeDrawable {

    int size;
    float yRatio;
    private static float[] diskOuterRadius = new float[] { 25, 25, 25, 25, 0, 0, 0, 0 }; //Массив с радиусами скругления для отрисовки дисков


    //Метод отрисовки прямоугольника со скругленными углами. Получиться диск.
    public DiskShape(int _size, float xRatio, float yRatio){
        super(new RoundRectShape(diskOuterRadius, null, null));

        this.yRatio = yRatio;// центр диска по оси y
        this.size = (int)(_size*24*xRatio); // центр диска по оси x
        this.unSelect();//отобразить диск как неативный

        setBound();

    }

    //Метод позиционирования компонетов
    public void setBound() {
        this.setBounds(0, 0, this.size, (int)(24*yRatio));
    }

    //Метод, выделяющий выбранный диск, изменяя его цвет
    public void select(){
        this.getPaint().setColor(0x88FF8844);
    }

    //Метод, возвращает значение цвета диска на стандартное
    public  void unSelect(){
        this.getPaint().setColor(0xFFFF8844);
    }

    //Shape -интерфейс Фигура
    //Canvas - класс, являющийся "Пустым холстом" для рисования чего-либо
    //Paint - интерфейс определяющий цвета, которые могут использоваться для графических операций
    protected void onDraw(Shape shape, Canvas canvas, Paint paint){
        canvas.save();
        //Переводим центр изображения влево
        //для отрисовки диска в цетре стержня
        canvas.translate(-size/2, 0);
        shape.draw(canvas, paint);

        canvas.restore();
    }

}
