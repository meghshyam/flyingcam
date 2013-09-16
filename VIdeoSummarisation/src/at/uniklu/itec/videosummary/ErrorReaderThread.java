package at.uniklu.itec.videosummary;

import java.io.InputStream;
import java.io.IOException;

/**
 * A video summary generator based on FFMPEG.
 * Date: 11.07.2008
 * Time: 10:38:58
 * (c) 2008 Mathias Lux, Klaus Schoeffmann & Markus Waltl, ITEC, Klagenfurt University
 *
 * This source code is licensed under GPL. That means it is is free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this programm; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author Mathias Lux, mathias@juggle.at
 * @author Markus Waltl
 */
public class ErrorReaderThread implements Runnable{
    InputStream errorStream;

    public ErrorReaderThread(InputStream errorStream) {
        this.errorStream = errorStream;
    }

    public void run() {
        int tmp=-1;
        try {
            while ((tmp=errorStream.read())>-1) {
                // System.out.write(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
