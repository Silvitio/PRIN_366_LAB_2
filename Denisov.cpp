
#pragma warning(disable:4996)
#include <iostream>
#include <stdio.h>
#include <conio.h>
#include <math.h>
#include "testing.h"

int main()
{
    int x_m, y_m, x_a, y_a, x_b, y_b, x_c, y_c, x_d, y_d; //координаты точек 
    int Left_border, Right_border, Top_border, Bottom_border; //координаты левой, правой, верхней и нижней границ прямоугольника

    //ввести координаты точек

    scanf("%d %d %d %d %d %d %d %d %d %d", &x_m, &y_m, &x_a, &y_a, &x_b, &y_b, &x_c, &y_c, &x_d, &y_d);

    // Проверка неверных входных данных...

    //заданы точки вне диапазона(+-1000)
    if(!(x_m <= 9999 && x_m >= -1000 && y_m <= 1000 && y_m >= -1000 && x_a <= 1000 && x_a >= -1000 && y_a <= 1000 && y_a >= -1000 && x_b <= 1000 && x_b >= -1000 && y_b <= 1000 && y_b >= -1000 && x_c <= 1000 && x_c >= -1000 && y_c <= 1000 && y_c >= -1000 && x_d <= 1000 && x_d >= -1000 && y_d <= 1000 && y_d >= -1000))
    {
        error_printf("coordinates out of range!");
        return 0;
    }

    //abcd - не прямоугольник
    int abcd_is_not_rectangle = ((x_a == x_b) && (x_a == x_c) && (x_a == x_d) && (x_b == x_c) && (x_b == x_d) && (x_c == x_d)) || ((y_a == y_b) && (y_a == y_c) && (y_a == y_d) && (y_b == y_c) && (y_b == y_d) && (y_c == y_d));
    if (abcd_is_not_rectangle)
    {
        error_printf("abcd is not a rectangle!");
        return 0;
    }

    //решение задачи

    //Определить, находится ли точка внутри прямоугольника, включая его границу

    //нахождение левой границы
    Left_border = min(min(x_a,x_b), min(x_c,x_d));

    //нахождение правой границы
    Right_border = max(max(x_a, x_b), max(x_c, x_d));

    //нахождение верхней границы
    Top_border = max(max(y_a, y_b), max(y_c, y_d));

    //нахождение нижней границы
    Bottom_border = min(min(y_a, y_b), min(y_c, y_d));

    //точка m в прямоугольнике
    int m_in_rectangle = (x_m >= Left_border && x_m <= Right_border && y_m <= Top_border && y_m >= Bottom_border);

    //Определить, находится ли точка внутри окружности, описывающей прямоугольник (не включая ее границу)

    //точка m в круге
    int m_in_oval (pow(fabs((Right_border - fabs(Right_border - Left_border) / 2) - x_m), 2) + pow(fabs((Top_border - fabs(Top_border - Bottom_border) / 2) - y_m), 2) < (pow(fabs(Right_border - Left_border) / 2, 2) + pow(fabs(Top_border - Bottom_border) / 2, 2)));

    //напечатать лежит ли точка между прямоугольником и кругом

    if (!m_in_rectangle && m_in_circle)
    {
        printf("1");
    }
    else
    {
        printf("0");
    }
}