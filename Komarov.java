package brokenrobotgame.model;

import brokenrobotgame.model.events.RobotActionEvent;
import brokenrobotgame.model.events.RobotActionListener;
import brokenrobotgame.model.navigation.Direction;
import brokenrobotgame.model.navigation.MiddlePosition;
import brokenrobotgame.model.navigation.CellPosition;
import java.util.ArrayList;


/*
 * Robot - ����� ������������� �� ����, ���� ������� ����� ���������� ���������; 
 * �������������� ����������, ���� ����� ������; ����� ������������ ���������,
 * ����������� � ����
 */
public class Robot
{
    // ------------------- ������������� ����� � ������� ����� -----------------
    private GameField _field;

    public GameField field() {
        return _field;
    }

    public void removeField() {
        _field = null;
    }

    public Robot(GameField field, CellPosition startPos) {
        _field = field;
        _position = startPos;
    }

    // ------------------- ����� "��������" �� ��������� � ����� �� ������ -----------------
    private Battery _battery;
    private int _charge;

    public void useBattery(Battery outBattery) {
        if (_field == null) {
            throw new NullPointerException("����� �� ����������� ����. �� ���������.");
        }

        // ����� ��������� ������ ���������� ����� � ������� ��� ���� ��� ����
        if (outBattery == null || (outBattery.position() != null && !outBattery.position().equals(_position))) {
            throw new IllegalStateException("��������� ������ ���������� �� ������� ������ ��� ���� ��� ����.");
        }

        // ���������� ������ ���������, ���� ��� ��� �����������
        if (_battery != null) {
            _battery.destroy();
        }

        // ������������� ����� ���������
        _battery = outBattery;

        // ������� ��������� � �������� ����, ���� ��� ��������� �� ��
        if (outBattery.position() != null) {
            _field.removeBattery(outBattery);
        }

        // ���������� �������
        fireRobotAction();
    }
	
    public int amountOfCharge(){
        return _battery != null ? _battery.amountOfCharge() : 0;
    }

    public int chargeCapacity(){
        return _battery != null ? _battery.chargeCapacity() : 0;
    }
    
    protected void reduceCharge(int delta){
        if (_battery == null) {
            throw new IllegalArgumentException("������� �����������.");
        }
        _battery.reduceCharge(delta);
    }
	
    
    // ------------------- ����� ����� ��������� � ��������� ����� -----------------

    public void openCloseDoor(Direction direct){
        if (_field == null) {
            throw new NullPointerException("����� �� ����������� ����. �� ���������.");
        }

        if (amountOfCharge() > 0) { // ����� ������ ����� �����
            MiddlePosition doorPos = new MiddlePosition(_position, direct);
            Door door = _field.door(doorPos);

            if (door != null) { // ����� ������� �����
                // ������� ��� ������� �����
                if (door.isOpen()) {
                    door.close();
                } else {
                    door.open();
                }

                // ���������� �����
                reduceCharge(1);

                // ���������� �������
                fireRobotAction();
            }
        }
    }
    
    // ------------------- ������� ������ -----------------

    private CellPosition _position;
    
    public CellPosition position(){
        return _position;
    }
    
    protected boolean setPosition(CellPosition pos){
        _position = pos;
        return true;
    }
	

    // ------------------- ������� ������ -----------------

    public void makeMove(Direction direct) {
        if (amountOfCharge() > 0) // ����� ������ ����� �����
        {
            if (moveIsPossible(direct)) // ������ ���� ���� ������ � ��� ������ �� ������
            {
                // ������������ � ������ ������
                setPosition(position().next(direct));
                // ���������� �����
                reduceCharge(1);

                // ���������� �������
                fireRobotAction();
            }
        }
    }

    private boolean moveIsPossible(Direction direct) {
        if (_field == null) {
            throw new NullPointerException("����� �� ����������� ����. �� ���������.");
        }

        // ���� ��� �����������
        if (!position().hasNext(direct)) return false;

        MiddlePosition nextMiddlePos = new MiddlePosition(position(), direct);

        // ���������, ���� �� ����� �� ����
        if (_field.isWall(nextMiddlePos)) return false;

        // ���������, ���� �� �������� ����� �� ����
        Door door = _field.door(nextMiddlePos);
        if (door != null && !door.isOpen()) return false;

        return true;
    }
    
    // ---------------------- ��������� ������� -----------------------------

    private ArrayList<RobotActionListener> _listeners = new ArrayList<>();
    
    // ������������ ���������
    public void addRobotActionListener(RobotActionListener l) {
        _listeners.add(l);
    }
    
    // ����������� ���������
    public void removeRobotActionListener(RobotActionListener l) {
        _listeners.remove(l);
    }

    // ��������� ���������� � �������
    protected void fireRobotAction() {
        RobotActionEvent event = new RobotActionEvent(this);
        for (RobotActionListener listener : _listeners) {
            listener.robotMakedMove(event);
        }
    }
}