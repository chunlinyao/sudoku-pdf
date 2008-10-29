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
 * $Header$
 * $Revision$
 * $Date$
 */

package cn.js.nt.yao.sudokupdf;

import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * Sudoku PDF
 *
 * @author Yao Chunlin
 */
public class Main {
    private PDFRender render = new PDFRender(new A4Layout());

    Main() {
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.startApp(m);
    }

    public File createPDF(List<PrintRecord> sudokuList) {
        render.setData(sudokuList);

        return render.process();
    }

    private void startApp(final Main m) {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame main = new MainFrame(m);
                    main.setLocation(200, 200);
                    main.pack();
                    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    main.setVisible(true);
                }
            });
    }
}
