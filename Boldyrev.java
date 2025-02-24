package brokenrobotgame.model.robots;

import brokenrobotgame.model.GameField;
import brokenrobotgame.model.navigation.CellPosition;
import brokenrobotgame.model.skills.CellColoring;
import brokenrobotgame.model.skills.RadiationMeasuring;
import brokenrobotgame.model.tools.ChameleonBrush;
import brokenrobotgame.model.tools.Random_mSievertDosimeter;

import java.awt.*;

/*
 * RadiationMeasuringAndCellColoringRobot - модель робота, который помимо того, что обладает теми же умениями, что и AbstractRobot,
 * умеет закрашивать клетки игрового поля и измерять уровень радиации в них.
 */
public class RadiationMeasuringAndCellColoringRobot extends AbstractRobot implements RadiationMeasuring, CellColoring
{
    // ------------------- Порождение робота -----------------

    public RadiationMeasuringAndCellColoringRobot(GameField field) { super(field); }

    // ------------------- Инструменты робота -----------------

    private Random_mSievertDosimeter dosimeter = new Random_mSievertDosimeter(super.field());
    private ChameleonBrush brush = new ChameleonBrush(super.field());

    // ------------------- Стоимость (в зарядах) для закрашивания клетки -----------------

    private final int cellColoringPrice = 2;

    public int getCellColoringPrice() { return cellColoringPrice; }

    // ------------------- Измерение радиации роботом -----------------

    @Override
    public int measureRadiation(CellPosition position)
    {
        // Возвращаем случайное значение радиации, полученное от дозиметра
        return dosimeter.measureRadiation(position); // радиация от 0 до 2000 мЗв
    }

    // ------------------- Закраска клетки роботом -----------------

    @Override
    public void colorCell(CellPosition position, Color color)
    {
        if(amountОfСharge() >= cellColoringPrice)
        {
            brush.colorCell(position, color);  // Закрашиваем кистью заданную позицию на поле в необходимый цвет
            // Используем заряд
            reduceCharge(cellColoringPrice);
        }
    }
}
