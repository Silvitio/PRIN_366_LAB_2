package brokenrobotgame.model;

import brokenrobotgame.model.events.RobotActionEvent;
import brokenrobotgame.model.events.RobotActionListener;
import brokenrobotgame.model.navigation.Direction;
import brokenrobotgame.model.navigation.MiddlePosition;
import brokenrobotgame.model.navigation.CellPosition;
import java.util.ArrayList;


/*
 * Robot - может передвигаться по полю, если имеется заряд внутренней батарейки; 
 * самостоятельно определяет, куда может ходить; может использовать батарейки,
 * находящиеся в поле
 */
public class Robot
{
    // ------------------- Устанавливаем связь с игровым полем -----------------
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

    // ------------------- Робот "питается" от батарейки и может их менять -----------------
    private Battery _battery;

    public void useBattery(Battery outBattery) {
        if (_field == null) {
            throw new NullPointerException("Робот не принадлежит полю. Он уничтожен.");
        }

        // Новая батарейка должна находиться рядом с роботом или быть вне поля
        if (outBattery == null || (outBattery.position() != null && !outBattery.position().equals(_position))) {
            throw new IllegalStateException("Батарейка должна находиться на позиции робота или быть вне поля.");
        }

        // Уничтожаем старую батарейку, если она уже установлена
        if (_battery != null) {
            _battery.destroy();
        }

        // Устанавливаем новую батарейку
        _battery = outBattery;

        // Удаляем батарейку с игрового поля, если она находится на нём
        if (outBattery.position() != null) {
            _field.removeBattery(outBattery);
        }

        // Генерируем событие
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
            throw new IllegalArgumentException("Батарея отсутствует.");
        }
        _battery.reduceCharge(delta);
    }
	
    
    // ------------------- Робот может открывать и закрывать двери -----------------

    public void openCloseDoor(Direction direct){
        if (_field == null) {
            throw new NullPointerException("Робот не принадлежит полю. Он уничтожен.");
        }

        if (amountOfCharge() > 0) { // робот должен иметь заряд
            MiddlePosition doorPos = new MiddlePosition(_position, direct);
            Door door = _field.door(doorPos);

            if (door != null) { // перед роботом дверь
                // открыть или закрыть дверь
                if (door.isOpen()) {
                    door.close();
                } else {
                    door.open();
                }

                // Используем заряд
                reduceCharge(1);

                // Генерируем событие
                fireRobotAction();
            }
        }
    }
    
    // ------------------- Позиция робота -----------------

    private CellPosition _position;
    
    public CellPosition position(){
        return _position;
    }
    
    protected boolean setPosition(CellPosition pos){
        _position = pos;
        return true;
    }
	

    // ------------------- Двигаем робота -----------------

    public void makeMove(Direction direct) {
        if (amountOfCharge() > 0) // робот должен иметь заряд
        {
            if (moveIsPossible(direct)) // роботу есть куда ходить и ему ничего не мешает
            {
                // Перемещаемся в другую клетку
                setPosition(position().next(direct));
                // Используем заряд
                reduceCharge(1);

                // Генерируем событие
                fireRobotAction();
            }
        }
    }

    private boolean moveIsPossible(Direction direct) {
        if (_field == null) {
            throw new NullPointerException("Робот не принадлежит полю. Он уничтожен.");
        }

        // поле уже закончилось
        if (!position().hasNext(direct)) return false;

        MiddlePosition nextMiddlePos = new MiddlePosition(position(), direct);

        // Проверяем, есть ли стена на пути
        if (_field.isWall(nextMiddlePos)) return false;

        // Проверяем, есть ли закрытая дверь на пути
        Door door = _field.door(nextMiddlePos);
        if (door != null && !door.isOpen()) return false;

        return true;
    }
    
    // ---------------------- Порождает события -----------------------------

    private ArrayList<RobotActionListener> _listeners = new ArrayList<>();
    
    // присоединяет слушателя
    public void addRobotActionListener(RobotActionListener l) {
        _listeners.add(l);
    }
    
    // отсоединяет слушателя
    public void removeRobotActionListener(RobotActionListener l) {
        _listeners.remove(l);
    }

    // оповещает слушателей о событии
    protected void fireRobotAction() {
        RobotActionEvent event = new RobotActionEvent(this);
        for (RobotActionListener listener : _listeners) {
            listener.robotMakedMove(event);
        }
    }
}