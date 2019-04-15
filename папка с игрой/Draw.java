package com.example.hanoitower;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class Draw extends View {

    private Stack<DiskShape> leftRod, middleRod, rightRod; //Стеки для стержней с дисками
    private Stack<DiskShape> rodWithDiskSelected = null; //Стержень, в котором выбран диск

    Context context; //Интерфейс, предоставляющий глобальную информацию о среде приложения

    int no_of_disks, moves = 0; //переменные: для провеки колличества дисков в первом ряду; для подсчета колличества перемещений
    float x, y; // координаты прикосеовения к тачпаду
    float xRatio, yRatio; //координаты для работы с фигурами
    boolean isValidTouch = true; // служит для проверки на то, что пользователь нажал  на фигуру
    float bottomLimit, topLimit, leftLimitMiddleRod, rightLimitMiddleRod; // Крайние положения, за которые нельзя выводить фигуры

    //метод отрисовки дисков. _no_of_disks это колличество дисков, зависящее от выбранной сложности
    @SuppressWarnings("deprecation")
    public Draw(Context context, float width, float height, int _no_of_disks) {
        super(context);

        this.context = context;


        //Нарисуем на фоне каринку, изображающую три штыря
        setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),
                R.drawable.hanoi_background)));

        //Стеки, в которых храняться созданные диски
        leftRod = new Stack<DiskShape>();
        middleRod = new Stack<DiskShape>();
        rightRod = new Stack<DiskShape>();

        //Начальное положение дисков. Первый стержень
        xRatio = width / 480;
        yRatio = height / 320;
        no_of_disks = _no_of_disks;

        //Ограничение для расположения дисков
        bottomLimit = 20 * yRatio; //миниммально возможное нижнеее
        topLimit = 250 * yRatio; //Максимально возможное верхнее
        leftLimitMiddleRod = 165 * xRatio; // ограничение слева для центра столба
        rightLimitMiddleRod = 315 * xRatio; // ограничение справа для центра столба

        //Заполнение штыря дисками, в зависимости от сложности
        for (int i = _no_of_disks; i >= 1; i--) {
            leftRod.push(new DiskShape(i, xRatio, yRatio));
        }
    }

    //метод отрисовки дисков. Диски отрисовываются сперва для левого штыря, затем Canvas(холст) обновляется.
    // После этого отрисовываются диски на центральном и затем на правом штыре.
    public void onDraw(Canvas canvas) {

        // координаты левого штыря для дисплея с разрешением 480*320 должны быть (90, 226)
        canvas.translate(xRatio * 90, yRatio * 226);
        canvas.save();
        drawDisks(canvas, leftRod);
        canvas.restore();

        // для центальгного штыря (240, 226)
        canvas.translate(150 * xRatio, 0);
        canvas.save();
        drawDisks(canvas, middleRod);
        canvas.restore();

        // для правого штыря (390, 226)
        canvas.translate(150 * xRatio, 0);
        canvas.save();
        drawDisks(canvas, rightRod);
        canvas.restore();
    }

    //Метод, отрисовывающий диски. Ось Y направлена сверху вниз
    private void drawDisks(Canvas canvas, Stack<DiskShape> rod) {
        for (DiskShape disk : rod) {
            disk.draw(canvas);
            canvas.translate(0, -25 * yRatio);
        }
    }

    //Метод, обрабатывающий события с тачскрином
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            //координаты, в которых произошло касание
            x = event.getX();
            y = event.getY();

            //Проверка на то, было ли касание в область с дисками и если было, то определение с какого штыря снять диск
            if (y > bottomLimit && y < topLimit) {
                isValidTouch = true;
                if (x < leftLimitMiddleRod)
                    rodWithDiskSelected = leftRod;
                else if (x >= leftLimitMiddleRod && x <= rightLimitMiddleRod)
                    rodWithDiskSelected = middleRod;
                else
                    rodWithDiskSelected = rightRod;

                //если на штыре есть диск, выбрать его
                if (rodWithDiskSelected.size() != 0)
                    rodWithDiskSelected.lastElement().select();

            } else
                //Касание было произведено вне рабочей зоны
                isValidTouch = false;

            invalidate();

        }
        //если пользователь двигает по дисплею рукой в рабочей зоне и в выбранной области на штыре есть диск
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isValidTouch == true) {
                if (rodWithDiskSelected.size() != 0) {

                    //переменные для работы с выбранным диском
                    int mX = (int) (90 * xRatio);
                    int mY = (int) (250 * yRatio);

                    //если выбран центральный ряд
                    if (rodWithDiskSelected == middleRod) {
                        // mX=240;
                        mX += 150 * xRatio;
                    }
                    //Если выбран правый ряд
                    else if (rodWithDiskSelected == rightRod) {
                        // mX=390;
                        mX += 300 * xRatio;
                    }
                    //переменная, для определения отступа при расположении диска на штыре
                    int mm = (int) (rodWithDiskSelected.size() * 25 * yRatio);

                    //кооринаты по которым будет расположен диск
                    x = event.getX() - mX;
                    y = event.getY() - mY + mm;

                    //"насаживание" диска по указанным коодинатам
                    rodWithDiskSelected.lastElement().setBound();
                    rodWithDiskSelected.lastElement().getBounds()
                            .inset((int) x, (int) y);
                    invalidate();
                }
            }
        }
        //если пользователь убрал палец с дисплея и это произошло в рабочей зоне
        //берем координаты где пользователь убрал свой палец и вставляем диск по коодинатам ближайшего штыря
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isValidTouch == true)
                if (rodWithDiskSelected.size() != 0) {
                    x = event.getX();
                    y = event.getY();
                    rodWithDiskSelected.lastElement().setBound();
                    if (y > bottomLimit && y < topLimit) {
                        rodWithDiskSelected.lastElement().getBounds()
                                .inset((int) x, (int) y);
                        if (x < leftLimitMiddleRod) {
                            actionOnTouch(leftRod);
                        } else if (x >= leftLimitMiddleRod
                                && x <= rightLimitMiddleRod) {
                            actionOnTouch(middleRod);
                        } else
                            actionOnTouch(rightRod);
                    } else
                        rodWithDiskSelected.lastElement().unSelect();
                    invalidate();
                }

        }
        return true;
    }

    //метод, осуществляющий упорядочиваеие дисков их перемещение между стеками и отслеживает заполненность левого штыря
    private void actionOnTouch(Stack<DiskShape> touchedRod) {
        rodWithDiskSelected.lastElement().unSelect();
        rodWithDiskSelected.lastElement().setBound();

        if (isValidMove(touchedRod)) {
            touchedRod.push(rodWithDiskSelected.pop());
            moves++;
        }
        rodWithDiskSelected = null;
        invalidate();

        if (rightRod.size() == no_of_disks || middleRod.size() == no_of_disks) {
            ((Play) getContext()).gameOver(moves);
        }
    }

    //метод, проверяющий, было ли касание в рабочей зоне
    private boolean isValidMove(Stack<DiskShape> touchedRod) {
        return touchedRod.size() == 0
                || rodWithDiskSelected.lastElement().size < touchedRod
                .lastElement().size;
    }
}
