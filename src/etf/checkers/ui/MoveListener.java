package etf.checkers.ui;

import java.util.EventListener;

import etf.checkers.*;

/**
 * Defines an object which listens for MoveEvents. This class is used primarily
 * by ui.HumanPlayer.
 */
public interface MoveListener extends EventListener
{
    /**
     * Invoked when the target of the listener has selected a move.
     * @param e         a MoveEvent object
     */
    void moveSelected(MoveEvent e);
}

