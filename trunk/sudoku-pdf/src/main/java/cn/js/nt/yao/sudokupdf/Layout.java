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

import com.lowagie.text.Rectangle;


/**
 *
 * @author yaochunlin
 */
public abstract class Layout {
    abstract double getDx();

    abstract double getDy();

    abstract Rectangle getPageSize();

    abstract double getScale();

    abstract void postRender(int i, PDFRender render);

    abstract void preRender(int i, PDFRender render);

    abstract void setMode(Mode mode);
    static enum Mode {SUDOKU, ANSWER;
    }
}
