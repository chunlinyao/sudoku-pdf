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

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * Sudoku PDF
 *
 * @author Yao Chunlin
 */
public class Main {
    Main() {
    }

    public static void main(String[] args) {
        Main m = new Main();

        try {
            LookAndFeel laf = new NimbusLookAndFeel();
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        m.startApp(m);
    }

    public File createPDF(List<PrintRecord> sudokuList, String layoutName) {
        Layout layout;

        if ("Letter".equals(layoutName)) {
            layout = new LetterLayout();
        } else {
            layout = new A4Layout();
        }

        PDFRender render = new PDFRender(layout);

        render.setData(sudokuList);

        return render.process();
    }

    private void startApp(final Main m) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final JFrame main = new MainFrame(m);
                    main.setLocation(200, 200);
                    main.pack();

                    Dimension screenSize = Toolkit.getDefaultToolkit()
                                                  .getScreenSize();
                    main.setLocation((int) (screenSize.getWidth()
                        - main.getWidth()) / 2,
                        (int) (screenSize.getHeight() - main.getHeight()) / 2);
                    main.setVisible(true);
                }
            });
    }
}
