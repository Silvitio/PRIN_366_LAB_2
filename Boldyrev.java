package brokenrobotgame.model.robots;

import brokenrobotgame.model.GameField;
import brokenrobotgame.model.navigation.CellPosition;
import brokenrobotgame.model.skills.CellColoring;
import brokenrobotgame.model.skills.RadiationMeasuring;
import brokenrobotgame.model.tools.ChameleonBrush;
import brokenrobotgame.model.tools.Random_mSievertDosimeter;

import java.awt.*;

/*
 * RadiationMeasuringAndCellColoringRobot - ������ ������, ������� ������ ����, ��� �������� ���� �� ��������, ��� � AbstractRobot,
 * ����� ����������� ������ �������� ���� � �������� ������� �������� � ���.
 */
public class RadiationMeasuringAndCellColoringRobot extends AbstractRobot implements RadiationMeasuring, CellColoring, Measuring
{
    // ------------------- ���������� ������ -----------------

    public RadiationMeasuringAndCellColoringRobot(GameField field) { super(field); }

    // ------------------- ����������� ������ -----------------

    private Random_mSievertDosimeter dosimeter = new Random_mSievertDosimeter(super.field());
    private ChameleonBrush brush = new ChameleonBrush(super.field());

    // ------------------- ��������� (� �������) ��� ������������ ������ -----------------

    private final int cellColoringPrice = 4;

    public int getCellColoringPrice() { return cellColoringPrice; }

    // ------------------- ��������� �������� ������� -----------------

    @Override
    public double measureRadiation(CellPosition position)
    {
        // ���������� ��������� �������� ��������, ���������� �� ���������
        return dosimeter.measureRadiation(position); // �������� �� 0 �� 2000 ���
    }

    // ------------------- �������� ������ ������� -----------------

    @Override
    public void colorCell(CellPosition position, Color color)
    {
        if(amount�f�harge() > cellColoringPrice)
        {
            brush.colorCell(position, color);  // ����������� ������ �������� ������� �� ���� � ����������� ����
            // ���������� �����
            reduceCharge(cellColoringPrice);
        }
    }
}
