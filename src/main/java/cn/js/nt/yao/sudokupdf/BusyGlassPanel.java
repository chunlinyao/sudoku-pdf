/*
 *  Project: Sudoku PDF
 *
 *  Copyright 2008 yaochunlin.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 * ------------------------------------------------------------------------
 * $Id$
 * $Revision$
 * $Date$
 */
package cn.js.nt.yao.sudokupdf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 *
 * @author yaochunlin
 */
public class BusyGlassPanel extends JPanel implements KeyEventDispatcher {
    private JFrame frame;
    private List<JComponent> enabledList;

    public BusyGlassPanel(JFrame frame, List<JComponent> enabledList) {
        setLayout(new BorderLayout());
        setVisible(false);
        setOpaque(false);
        this.frame = frame;
        this.enabledList = enabledList;

        //        addMouseListener(this);
        //        addMouseMotionListener(this);
        final MouseAdapter mouseAdapter = new MouseAdapter() {
            };

        addMouseListener(new MouseAdapter() {
            });
        addMouseMotionListener(mouseAdapter);
        //        addFocusListener(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /*
       @Override
       public void mouseClicked(MouseEvent e) {
           reDispatchMouseEvent(e);
       }
       @Override
       public void mousePressed(MouseEvent e) {
           reDispatchMouseEvent(e);
       }
       @Override
       public void mouseReleased(MouseEvent e) {
           reDispatchMouseEvent(e);
       }
       @Override
       public void mouseEntered(MouseEvent e) {
       }
       @Override
       public void mouseExited(MouseEvent e) {
       }
       @Override
       public void mouseDragged(MouseEvent e) {
       }
       @Override
       public void mouseMoved(MouseEvent e) {
           reDispatchMouseEvent(e);
       }
       private void reDispatchMouseEvent(MouseEvent e) {
           Point glassPanelPoint = e.getPoint();
           Container container = frame.getContentPane();
           Component component = null;
       //        boolean inMenubar = false;
               Point containerPoint = SwingUtilities.convertPoint(this,
                       glassPanelPoint, container);
       //        if (containerPoint.y < 0) {
       //            inMenubar = true;
       //            container = frame.getJMenuBar();
       //            containerPoint = SwingUtilities.convertPoint(this, glassPanelPoint,
       //                    container);
       //        }
               component = SwingUtilities.getDeepestComponentAt(container,
                       containerPoint.x, containerPoint.y);
               if ((component != null)
                       && (inMenubar || enabledList.contains(component))) {
                   MouseEvent destE = SwingUtilities.convertMouseEvent(this, e,
                           component);
                   component.dispatchEvent(destE);
               }
           }
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // fill the component with the translucent color
        g2.setColor(new Color(255, 255, 255, 125));

        //        final int menuHeight = frame.getJMenuBar().getHeight();
        //        g2.fillRect(0, menuHeight, getWidth(), getHeight() - menuHeight);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    //    @Override
    //    public void focusGained(FocusEvent e) {
    //    }
    //
    //    @Override
    //    public void focusLost(FocusEvent e) {
    //        if (this.isVisible()) {
    //            // XXX removed .because cancel button must get focus.
    //            //requestFocus();
    //        }
    //    }
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);

        if (aFlag == true) {
            requestFocus();
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                .addKeyEventDispatcher(this);
        } else {
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                .removeKeyEventDispatcher(this);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        return SwingUtilities.isDescendingFrom(e.getComponent(), this);
    }
}
